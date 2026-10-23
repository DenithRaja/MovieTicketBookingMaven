package dao;

import dao.BookingDAO;
import model.*;
import org.junit.jupiter.api.Test;
import storage.MovieDataStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingDAOTest {

    // Helper method to create the required test data
    private MovieDataStorage createStorageWithShow() {
        MovieDataStorage storage = new MovieDataStorage();

        // Create and store a customer
        Customer customer = new Customer(
                1, "Dany", "dany@example.com", "9876543210"
        );
        storage.customers.put(customer.getUserId(), customer);

        // Create and store a screen
        Screen screen = new Screen(1, "Screen 1", 100);
        storage.screens.put(screen.getScreenId(), screen);

        // Create and store seats
        Seat seat1 = new Seat(1, "A1", "A", "PREMIUM");
        Seat seat2 = new Seat(2, "A2", "A", "REGULAR");

        storage.seats.put(seat1.getSeatId(), seat1);
        storage.seats.put(seat2.getSeatId(), seat2);

        storage.seatScreenIds.put(1, 1);
        storage.seatScreenIds.put(2, 1);

        // Create and store a show
        Show show = new Show(
                1,
                LocalDate.of(2026, 10, 20),
                LocalTime.of(18, 30),
                200.0,
                screen
        );

        storage.shows.put(show.getShowId(), show);

        return storage;
    }

    // Helper method to create a booking
    private Booking createBooking(int bookingId, Show show) {
        return new Booking(
                bookingId,
                LocalDateTime.now(),
                0.0,
                "BOOKED",
                show
        );
    }

    // 1. CREATE BOOKING

    @Test
    void checkCreateBooking() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        Booking booking = createBooking(1, storage.shows.get(1));

        boolean success = bookingDAO.createBooking(
                booking, 1, 1, List.of(1, 2)
        );

        assertTrue(success);
        assertEquals(booking, storage.bookings.get(1));
        assertEquals(1, storage.bookingCustomerIds.get(1));

        assertEquals(2, booking.getBookingSeats().size());
        assertEquals(400.0, booking.getTotalAmount(), 0.001);

        assertTrue(booking.getBookingSeats().get(0).isBooked());
        assertTrue(booking.getBookingSeats().get(1).isBooked());
    }

    @Test
    void checkCreateBookingWithInvalidCustomer() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        Booking booking = createBooking(1, storage.shows.get(1));

        boolean success = bookingDAO.createBooking(
                booking, 99, 1, List.of(1)
        );

        assertFalse(success);
        assertFalse(storage.bookings.containsKey(1));
    }

    @Test
    void checkCreateBookingWithInvalidShow() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        Booking booking = createBooking(1, storage.shows.get(1));

        boolean success = bookingDAO.createBooking(
                booking, 1, 99, List.of(1)
        );

        assertFalse(success);
        assertFalse(storage.bookings.containsKey(1));
    }

    @Test
    void checkCreateBookingWithInvalidSeat() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        Booking booking = createBooking(1, storage.shows.get(1));

        boolean success = bookingDAO.createBooking(
                booking, 1, 1, List.of(99)
        );

        assertFalse(success);
        assertFalse(storage.bookings.containsKey(1));
    }

    @Test
    void checkCreateBookingWithSeatFromAnotherScreen() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        Screen anotherScreen = new Screen(2, "Screen 2", 50);
        storage.screens.put(2, anotherScreen);

        Seat anotherSeat = new Seat(3, "B1", "B", "REGULAR");
        storage.seats.put(3, anotherSeat);
        storage.seatScreenIds.put(3, 2);

        Booking booking = createBooking(1, storage.shows.get(1));

        boolean success = bookingDAO.createBooking(
                booking, 1, 1, List.of(3)
        );

        assertFalse(success);
        assertFalse(storage.bookings.containsKey(1));
    }

    @Test
    void checkCreateBookingWithDuplicateBookingId() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        Booking firstBooking = createBooking(1, storage.shows.get(1));

        assertTrue(bookingDAO.createBooking(
                firstBooking, 1, 1, List.of(1)
        ));

        Booking duplicateBooking = createBooking(1, storage.shows.get(1));

        boolean success = bookingDAO.createBooking(
                duplicateBooking, 1, 1, List.of(2)
        );

        assertFalse(success);
        assertEquals(1, storage.bookings.size());
    }

    @Test
    void checkCreateBookingWithAlreadyBookedSeat() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        Booking firstBooking = createBooking(1, storage.shows.get(1));

        assertTrue(bookingDAO.createBooking(
                firstBooking, 1, 1, List.of(1)
        ));

        Booking secondBooking = createBooking(2, storage.shows.get(1));

        boolean success = bookingDAO.createBooking(
                secondBooking, 1, 1, List.of(1)
        );

        assertFalse(success);
        assertFalse(storage.bookings.containsKey(2));
    }

    // 2. FIND BOOKING BY ID

    @Test
    void checkFindBookingById() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        Booking booking = createBooking(1, storage.shows.get(1));

        assertTrue(bookingDAO.createBooking(
                booking, 1, 1, List.of(1)
        ));

        Optional<Booking> result = bookingDAO.findById(1);

        assertTrue(result.isPresent());
        assertEquals(booking, result.get());
    }

    @Test
    void checkFindBookingByInvalidId() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        Optional<Booking> result = bookingDAO.findById(99);

        assertTrue(result.isEmpty());
    }

    // 3. FIND BOOKINGS BY CUSTOMER ID

    @Test
    void checkFindBookingsByCustomerId() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        Booking booking1 = createBooking(1, storage.shows.get(1));
        Booking booking2 = createBooking(2, storage.shows.get(1));

        assertTrue(bookingDAO.createBooking(
                booking1, 1, 1, List.of(1)
        ));

        assertTrue(bookingDAO.createBooking(
                booking2, 1, 1, List.of(2)
        ));

        List<Booking> bookings = bookingDAO.findByCustomerId(1);

        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking1));
        assertTrue(bookings.contains(booking2));
    }

    @Test
    void checkFindBookingsByInvalidCustomerId() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        List<Booking> bookings = bookingDAO.findByCustomerId(99);

        assertTrue(bookings.isEmpty());
    }

    // 4. FIND BOOKINGS BY SHOW ID

    @Test
    void checkFindBookingsByShowId() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        Booking booking = createBooking(1, storage.shows.get(1));

        assertTrue(bookingDAO.createBooking(
                booking, 1, 1, List.of(1)
        ));

        List<Booking> bookings = bookingDAO.findByShowId(1);

        assertEquals(1, bookings.size());
        assertTrue(bookings.contains(booking));
    }

    @Test
    void checkFindBookingsByInvalidShowId() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        List<Booking> bookings = bookingDAO.findByShowId(99);

        assertTrue(bookings.isEmpty());
    }

    // 5. FIND ALL BOOKINGS

    @Test
    void checkFindAllBookings() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        Booking booking1 = createBooking(1, storage.shows.get(1));
        Booking booking2 = createBooking(2, storage.shows.get(1));

        assertTrue(bookingDAO.createBooking(
                booking1, 1, 1, List.of(1)
        ));

        assertTrue(bookingDAO.createBooking(
                booking2, 1, 1, List.of(2)
        ));

        List<Booking> bookings = bookingDAO.findAll();

        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking1));
        assertTrue(bookings.contains(booking2));
    }

    @Test
    void checkFindAllBookingsWhenEmpty() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        List<Booking> bookings = bookingDAO.findAll();

        assertTrue(bookings.isEmpty());
    }

    // 6. CANCEL BOOKING

    @Test
    void checkCancelBooking() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        Booking booking = createBooking(1, storage.shows.get(1));

        assertTrue(bookingDAO.createBooking(
                booking, 1, 1, List.of(1)
        ));

        boolean success = bookingDAO.cancelBooking(1);

        assertTrue(success);
        assertEquals("CANCELLED", booking.getStatus());
    }

    @Test
    void checkCancelBookingWithInvalidId() {

        MovieDataStorage storage = createStorageWithShow();
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);

        boolean success = bookingDAO.cancelBooking(99);

        assertFalse(success);
    }
}