package dao;

import dao.PaymentDAO;
import model.Booking;
import model.Payment;
import model.Screen;
import model.Show;
import storage.MovieDataStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentDataStorageDAOTest {

    private MovieDataStorage storage;
    private PaymentDAO paymentDAO;

    @BeforeEach
    void setUp() {
        storage = new MovieDataStorage();
        paymentDAO = new PaymentDataStorageDAO(storage);

        Screen screen = new Screen(1, "Screen 1", 100);

        Show show = new Show(
                1,
                LocalDate.now(),
                LocalTime.of(18, 30),
                500.0,
                screen
        );

        Booking booking = new Booking(
                1,
                LocalDateTime.now(),
                500.0,
                "BOOKED",
                show
        );

        storage.bookings.put(1, booking);
    }

    // =========================
    // ADD TESTS
    // =========================

    @Test
    void testAddPayment() {
        Payment payment = new Payment(1, 500.0, "UPI", "PENDING");

        assertTrue(paymentDAO.add(payment, 1));
        assertEquals(payment, storage.payments.get(1));
        assertEquals(1, storage.paymentBookingIds.get(1));
    }

    @Test
    void testAddPaymentWithNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentDAO.add(null, 1)
        );
    }

    @Test
    void testAddPaymentWithInvalidBookingId() {
        Payment payment = new Payment(1, 500.0, "UPI", "PENDING");

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentDAO.add(payment, 0)
        );
    }

    @Test
    void testAddPaymentWithNonExistingBooking() {
        Payment payment = new Payment(1, 500.0, "UPI", "PENDING");

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentDAO.add(payment, 999)
        );
    }

    @Test
    void testAddPaymentWithInvalidPaymentId() {
        Payment payment = new Payment(0, 500.0, "UPI", "PENDING");

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentDAO.add(payment, 1)
        );
    }

    @Test
    void testAddDuplicatePayment() {
        Payment payment = new Payment(1, 500.0, "UPI", "PENDING");
        paymentDAO.add(payment, 1);

        Payment duplicate = new Payment(1, 600.0, "CARD", "PENDING");

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentDAO.add(duplicate, 1)
        );
    }

    @Test
    void testAddPaymentWithInvalidDetails() {
        Payment payment = new Payment(1, -500.0, "UPI", "PENDING");

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentDAO.add(payment, 1)
        );
    }

    // =========================
    // UPDATE TESTS
    // =========================

    @Test
    void testUpdatePayment() {
        Payment payment = new Payment(1, 500.0, "UPI", "PENDING");
        paymentDAO.add(payment, 1);

        Payment updatedPayment = new Payment(
                1, 700.0, "CARD", "PENDING"
        );

        assertTrue(paymentDAO.update(updatedPayment));
        assertEquals(updatedPayment, storage.payments.get(1));
    }

    @Test
    void testUpdatePaymentWithNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentDAO.update(null)
        );
    }

    @Test
    void testUpdatePaymentWithInvalidId() {
        Payment payment = new Payment(0, 500.0, "UPI", "PENDING");

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentDAO.update(payment)
        );
    }

    @Test
    void testUpdateNonExistingPayment() {
        Payment payment = new Payment(999, 500.0, "UPI", "PENDING");

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentDAO.update(payment)
        );
    }

    @Test
    void testUpdatePaymentWithInvalidDetails() {
        Payment payment = new Payment(1, 500.0, "UPI", "PENDING");
        paymentDAO.add(payment, 1);

        Payment invalidPayment = new Payment(
                1, -500.0, "UPI", "PENDING"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentDAO.update(invalidPayment)
        );
    }

    // =========================
    // FIND BY ID TESTS
    // =========================

    @Test
    void testFindById() {
        Payment payment = new Payment(1, 500.0, "UPI", "PENDING");
        paymentDAO.add(payment, 1);

        assertTrue(paymentDAO.findById(1).isPresent());
        assertEquals(payment, paymentDAO.findById(1).get());
    }

    @Test
    void testFindByIdNotFound() {
        assertTrue(paymentDAO.findById(999).isEmpty());
    }

    @Test
    void testFindByIdWithInvalidId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentDAO.findById(0)
        );
    }

    // =========================
    // FIND BY BOOKING ID TESTS
    // =========================

    @Test
    void testFindByBookingId() {
        Payment payment1 = new Payment(1, 500.0, "UPI", "PENDING");
        Payment payment2 = new Payment(2, 300.0, "CARD", "PENDING");

        paymentDAO.add(payment1, 1);
        paymentDAO.add(payment2, 1);

        List<Payment> payments = paymentDAO.findByBookingId(1);

        assertEquals(2, payments.size());
        assertTrue(payments.contains(payment1));
        assertTrue(payments.contains(payment2));
    }

    @Test
    void testFindByBookingIdWithNoPayments() {
        List<Payment> payments = paymentDAO.findByBookingId(1);

        assertTrue(payments.isEmpty());
    }

    @Test
    void testFindByBookingIdWithInvalidId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentDAO.findByBookingId(0)
        );
    }

    @Test
    void testFindByBookingIdWithNonExistingBooking() {
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentDAO.findByBookingId(999)
        );
    }

    // =========================
    // FIND ALL TESTS
    // =========================

    @Test
    void testFindAllPayments() {
        Payment payment1 = new Payment(1, 500.0, "UPI", "PENDING");
        Payment payment2 = new Payment(2, 300.0, "CARD", "PENDING");

        paymentDAO.add(payment1, 1);
        paymentDAO.add(payment2, 1);

        List<Payment> payments = paymentDAO.findAll();

        assertEquals(2, payments.size());
        assertTrue(payments.contains(payment1));
        assertTrue(payments.contains(payment2));
    }

    @Test
    void testFindAllPaymentsWhenEmpty() {
        List<Payment> payments = paymentDAO.findAll();

        assertTrue(payments.isEmpty());
    }
}