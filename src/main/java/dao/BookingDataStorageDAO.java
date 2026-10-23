package dao;

import model.Booking;
import model.BookingSeat;
import model.Seat;
import model.Show;
import storage.MovieDataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingDataStorageDAO implements BookingDAO {
    private final MovieDataStorage storage;

    public BookingDataStorageDAO(MovieDataStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean createBooking(Booking booking, int customerId, int showId, List<Integer> seatIds) {
        if (booking == null || seatIds == null || seatIds.isEmpty()) return false;
        if (!storage.customers.containsKey(customerId)) return false;

        Show show = storage.shows.get(showId);
        if (show == null) return false;
        if (storage.bookings.containsKey(booking.getBookingId())) return false;

        int expectedScreenId = show.getScreen().getScreenId();
        for (Integer seatId : seatIds) {
            if (seatId == null || !storage.seats.containsKey(seatId)) return false;

            Integer screenId = storage.seatScreenIds.get(seatId);
            if (screenId == null || screenId != expectedScreenId) return false;
        }

        synchronized (show) {
            List<Booking> activeShowBookings = new ArrayList<>();
            for (Booking b : storage.bookings.values()) {
                if (b.getShow().getShowId() == showId) {
                    activeShowBookings.add(b);
                }
            }

            for (Integer seatId : seatIds) {
                for (Booking existingBooking : activeShowBookings) {
                    for (BookingSeat occupiedSeat : existingBooking.getBookingSeats()) {
                        if (occupiedSeat.getSeat().getSeatId() == seatId && occupiedSeat.isBooked()) {
                            return false;
                        }
                    }
                }
            }

            List<BookingSeat> temporarySeatsList = new ArrayList<>();
            int baseIndex = storage.bookingSeats.size();

            for (int i = 0; i < seatIds.size(); i++) {
                Seat seat = storage.seats.get(seatIds.get(i));
                BookingSeat bookingSeat = new BookingSeat(baseIndex + 1 + i, show.getBaseTicketPrice(), "BOOKED", seat);
                temporarySeatsList.add(bookingSeat);
            }

            for (BookingSeat bs : temporarySeatsList) {
                booking.addBookingSeat(bs);
                storage.bookingSeats.add(bs);
            }

            booking.calculateTotal();
            storage.bookings.put(booking.getBookingId(), booking);
            System.out.println("Saved booking ID: " + booking.getBookingId());

            for (BookingSeat bs : booking.getBookingSeats()) {
                System.out.println("Seat " + bs.getSeat().getSeatId() + " status: " + bs.getStatus());
            }
            storage.bookingCustomerIds.put(booking.getBookingId(), customerId);
            return true;
        }
    }

    @Override
    public Optional<Booking> findById(int bookingId) {
        Booking booking = storage.bookings.get(bookingId);
        if (booking == null) {
            return Optional.empty();
        }
        return Optional.of(booking);
    }

    @Override
    public List<Booking> findByCustomerId(int customerId) {
        List<Booking> customerBookings = new ArrayList<>();
        for (Integer bookingId : storage.bookingCustomerIds.keySet()) {
            if (storage.bookingCustomerIds.get(bookingId) == customerId) {
                Booking booking = storage.bookings.get(bookingId);
                if (booking != null) {
                    customerBookings.add(booking);
                }
            }
        }
        return customerBookings;
    }

    @Override
    public List<Booking> findByShowId(int showId) {
        List<Booking> showBookings = new ArrayList<>();
        for (Booking booking : storage.bookings.values()) {
            if (booking.getShow() != null && booking.getShow().getShowId() == showId) {
                showBookings.add(booking);
            }
        }
        return showBookings;
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(storage.bookings.values());
    }


    @Override
    public boolean cancelBooking(int bookingId) {
        Booking booking = storage.bookings.get(bookingId);

        if (booking == null) {
            return false;
        }

        if (!booking.isConfirmed()) {
            return false;
        }

        synchronized (booking.getShow()) {
            booking.cancelBooking();

            for (BookingSeat bookingSeat : booking.getBookingSeats()) {
                bookingSeat.cancel();
            }
        }

        return true;
    }

}