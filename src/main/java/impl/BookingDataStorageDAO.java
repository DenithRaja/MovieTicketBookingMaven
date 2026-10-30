
package impl;

import dao.BookingDAO;
import dao.CustomerDAO;
import dao.SeatDAO;
import dao.ShowDAO;
import model.Booking;
import model.BookingSeat;
import model.Seat;
import model.Show;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingDataStorageDAO implements BookingDAO {

    private final Map<Integer, Booking> bookings = new LinkedHashMap<>();
    private final Map<Integer, Integer> bookingCustomerIds = new LinkedHashMap<>();
    private final List<BookingSeat> bookingSeats = new ArrayList<>();

    private final AtomicInteger bookingIdSequence =
            new AtomicInteger(0);

    private final AtomicInteger bookingSeatIdSequence =
            new AtomicInteger(0);

    private final Object storageLock = new Object();

    private final CustomerDAO customerDAO;
    private final ShowDAO showDAO;
    private final SeatDAO seatDAO;

    public BookingDataStorageDAO(
            CustomerDAO customerDAO,
            ShowDAO showDAO,
            SeatDAO seatDAO) {

        if (customerDAO == null || showDAO == null || seatDAO == null) {
            throw new IllegalArgumentException(
                    "DAO dependencies cannot be null."
            );
        }

        this.customerDAO = customerDAO;
        this.showDAO = showDAO;
        this.seatDAO = seatDAO;
    }

    @Override
    public boolean createBooking(
            Booking booking,
            int customerId,
            int showId,
            List<Integer> seatIds) {

        if (booking == null || seatIds == null || seatIds.isEmpty()) {
            return false;
        }

        if (customerId <= 0 || showId <= 0) {
            return false;
        }

        if (customerDAO.findById(customerId).isEmpty()) {
            return false;
        }

        Optional<Show> showOptional = showDAO.findById(showId);

        if (showOptional.isEmpty()) {
            return false;
        }

        Show show = showOptional.get();

        if (show.getScreen() == null
                || show.getScreen().getScreenId() <= 0) {
            return false;
        }

        double ticketPrice = show.getBaseTicketPrice();

        if (!Double.isFinite(ticketPrice) || ticketPrice < 0) {
            return false;
        }

        Set<Integer> uniqueSeatIds = new HashSet<>();

        for (Integer seatId : seatIds) {
            if (seatId == null || seatId <= 0
                    || !uniqueSeatIds.add(seatId)) {
                return false;
            }
        }

        int expectedScreenId = show.getScreen().getScreenId();

        List<Seat> screenSeats =
                seatDAO.findByScreenId(expectedScreenId);

        Set<Integer> screenSeatIds = new HashSet<>();

        for (Seat screenSeat : screenSeats) {
            screenSeatIds.add(screenSeat.getSeatId());
        }

        List<Seat> selectedSeats = new ArrayList<>();

        for (Integer seatId : seatIds) {

            if (!screenSeatIds.contains(seatId)) {
                return false;
            }

            Optional<Seat> seatOptional = seatDAO.findById(seatId);

            if (seatOptional.isEmpty()) {
                return false;
            }

            selectedSeats.add(seatOptional.get());
        }

        double totalAmount = ticketPrice * selectedSeats.size();

        if (!Double.isFinite(totalAmount)) {
            return false;
        }

        synchronized (show) {

            List<Booking> activeShowBookings = findByShowId(showId);

            // Check whether any selected seat is already booked.
            for (Booking existingBooking : activeShowBookings) {

                for (BookingSeat existingSeat :
                        existingBooking.getBookingSeats()) {

                    if (existingSeat.getSeat() != null
                            && uniqueSeatIds.contains(
                            existingSeat.getSeat().getSeatId())
                            && existingSeat.isBooked()) {

                        return false;
                    }
                }
            }

            int bookingId = bookingIdSequence.incrementAndGet();

            int nextBookingSeatId =
                    bookingSeatIdSequence.get();

            List<BookingSeat> newBookingSeats = new ArrayList<>();

            // Prepare booking-seat records with unique IDs.
            for (Seat seat : selectedSeats) {

                int bookingSeatId =
                        bookingSeatIdSequence.incrementAndGet();

                newBookingSeats.add(
                        new BookingSeat(
                                bookingSeatId,
                                ticketPrice,
                                "BOOKED",
                                seat
                        )
                );
            }

            // Prepare the booking object.
            booking.setBookingId(bookingId);
            booking.setBookingDate(
                    booking.getBookingDate() == null
                            ? LocalDateTime.now()
                            : booking.getBookingDate()
            );
            booking.setShow(show);
            booking.setStatus("BOOKED");

            booking.getBookingSeats().clear();

            for (BookingSeat bookingSeat : newBookingSeats) {
                booking.addBookingSeat(bookingSeat);
            }

            booking.setTotalAmount(totalAmount);

            // Store all booking information together.
            synchronized (storageLock) {

                bookings.put(bookingId, booking);
                bookingCustomerIds.put(bookingId, customerId);
                bookingSeats.addAll(newBookingSeats);
            }

            return true;
        }
    }

    @Override
    public Optional<Booking> findById(int bookingId) {

        if (bookingId <= 0) {
            return Optional.empty();
        }

        synchronized (storageLock) {
            return Optional.ofNullable(bookings.get(bookingId));
        }
    }

    @Override
    public List<Booking> findByCustomerId(int customerId) {

        List<Booking> customerBookings = new ArrayList<>();

        if (customerId <= 0) {
            return customerBookings;
        }

        synchronized (storageLock) {

            for (Map.Entry<Integer, Integer> entry :
                    bookingCustomerIds.entrySet()) {

                if (entry.getValue() == customerId) {

                    Booking booking = bookings.get(entry.getKey());

                    if (booking != null) {
                        customerBookings.add(booking);
                    }
                }
            }
        }

        return customerBookings;
    }

    @Override
    public List<Booking> findByShowId(int showId) {

        List<Booking> showBookings = new ArrayList<>();

        if (showId <= 0) {
            return showBookings;
        }

        synchronized (storageLock) {

            for (Booking booking : bookings.values()) {

                if (booking.getShow() != null
                        && booking.getShow().getShowId() == showId) {

                    showBookings.add(booking);
                }
            }
        }

        return showBookings;
    }

    @Override
    public List<Booking> findAll() {

        synchronized (storageLock) {
            return new ArrayList<>(bookings.values());
        }
    }

    @Override
    public boolean cancelBooking(int bookingId) {

        if (bookingId <= 0) {
            return false;
        }

        Booking booking;

        synchronized (storageLock) {
            booking = bookings.get(bookingId);
        }

        if (booking == null || !booking.isConfirmed()) {
            return false;
        }

        Show show = booking.getShow();

        if (show == null) {
            return false;
        }

        synchronized (show) {

            synchronized (storageLock) {

                if (bookings.get(bookingId) != booking
                        || !booking.isConfirmed()) {
                    return false;
                }

                booking.cancelBooking();

                for (BookingSeat bookingSeat :
                        booking.getBookingSeats()) {

                    bookingSeat.cancel();
                }

                return true;
            }
        }
    }
}
