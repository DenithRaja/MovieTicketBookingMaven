package service;

import dao.BookingDAO;
import dao.CustomerDAO;
import dao.SeatDAO;
import dao.ShowDAO;
import model.Booking;
import model.Customer;
import model.Seat;
import model.Show;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BookingService {

    private final BookingDAO bookingDAO;
    private final CustomerDAO customerDAO;
    private final ShowDAO showDAO;
    private final SeatDAO seatDAO;

    public BookingService(
            BookingDAO bookingDAO,
            CustomerDAO customerDAO,
            ShowDAO showDAO,
            SeatDAO seatDAO) {

        if (bookingDAO == null || customerDAO == null
                || showDAO == null || seatDAO == null) {
            throw new IllegalArgumentException(
                    "DAO dependencies cannot be null."
            );
        }

        this.bookingDAO = bookingDAO;
        this.customerDAO = customerDAO;
        this.showDAO = showDAO;
        this.seatDAO = seatDAO;
    }

    public boolean createBooking(
            Booking booking,
            int customerId,
            int showId,
            List<Integer> seatIds) {

        if (booking == null) {
            throw new IllegalArgumentException(
                    "Booking cannot be null."
            );
        }

        validatePositiveId(customerId, "Customer ID");
        validatePositiveId(showId, "Show ID");

        if (booking.getBookingDate() == null) {
            throw new IllegalArgumentException(
                    "Booking date cannot be null."
            );
        }

        if (booking.getBookingDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Booking date cannot be in the future."
            );
        }

        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Select at least one seat."
            );
        }

        if (seatIds.stream().anyMatch(
                id -> id == null || id <= 0)) {
            throw new IllegalArgumentException(
                    "Seat IDs must be greater than zero."
            );
        }

        Set<Integer> uniqueSeatIds = new HashSet<>(seatIds);

        if (uniqueSeatIds.size() != seatIds.size()) {
            throw new IllegalArgumentException(
                    "Duplicate seat IDs are not allowed."
            );
        }

        Optional<Customer> customer =
                customerDAO.findById(customerId);

        if (customer.isEmpty()) {
            throw new IllegalArgumentException(
                    "Customer with ID " + customerId
                            + " does not exist."
            );
        }

        Optional<Show> showOptional = showDAO.findById(showId);

        if (showOptional.isEmpty()) {
            throw new IllegalArgumentException(
                    "Show with ID " + showId + " does not exist."
            );
        }

        Show show = showOptional.get();

        if (!show.isUpcoming()) {
            throw new IllegalStateException(
                    "Tickets can only be booked for upcoming shows."
            );
        }

        if (show.getScreen() == null) {
            throw new IllegalArgumentException(
                    "The selected show has no screen."
            );
        }

        int screenId = show.getScreen().getScreenId();

        List<Seat> screenSeats = seatDAO.findByScreenId(screenId);

        Set<Integer> availableScreenSeatIds = new HashSet<>();

        for (Seat seat : screenSeats) {
            availableScreenSeatIds.add(seat.getSeatId());
        }

        for (Integer seatId : uniqueSeatIds) {
            if (!availableScreenSeatIds.contains(seatId)) {
                throw new IllegalArgumentException(
                        "Seat ID " + seatId
                                + " does not belong to the selected show's screen."
                );
            }
        }

        return bookingDAO.createBooking(
                booking,
                customerId,
                showId,
                seatIds
        );
    }

    public Optional<Booking> findBookingById(int bookingId) {
        validatePositiveId(bookingId, "Booking ID");

        return bookingDAO.findById(bookingId);
    }

    public List<Booking> findBookingsByCustomerId(int customerId) {
        validatePositiveId(customerId, "Customer ID");

        if (customerDAO.findById(customerId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Customer with ID " + customerId
                            + " does not exist."
            );
        }

        return bookingDAO.findByCustomerId(customerId);
    }

    public List<Booking> findBookingsByShowId(int showId) {
        validatePositiveId(showId, "Show ID");

        if (showDAO.findById(showId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Show with ID " + showId + " does not exist."
            );
        }

        return bookingDAO.findByShowId(showId);
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.findAll();
    }

    public boolean cancelBooking(int bookingId) {
        validatePositiveId(bookingId, "Booking ID");

        Optional<Booking> bookingOptional =
                bookingDAO.findById(bookingId);

        if (bookingOptional.isEmpty()) {
            throw new IllegalArgumentException(
                    "Booking with ID " + bookingId
                            + " does not exist."
            );
        }

        Booking booking = bookingOptional.get();

        if (!booking.isConfirmed()) {
            throw new IllegalStateException(
                    "Only confirmed bookings can be cancelled."
            );
        }

        return bookingDAO.cancelBooking(bookingId);
    }

    private void validatePositiveId(int id, String fieldName) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero."
            );
        }
    }
}
