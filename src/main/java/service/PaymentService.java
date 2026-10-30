package service;

import dao.BookingDAO;
import dao.PaymentDAO;
import model.Booking;
import model.Payment;

import java.util.List;
import java.util.Optional;

public class PaymentService {

    private final PaymentDAO paymentDAO;
    private final BookingDAO bookingDAO;

    public PaymentService(
            PaymentDAO paymentDAO,
            BookingDAO bookingDAO) {

        if (paymentDAO == null || bookingDAO == null) {
            throw new IllegalArgumentException(
                    "DAO dependencies cannot be null."
            );
        }

        this.paymentDAO = paymentDAO;
        this.bookingDAO = bookingDAO;
    }


    public boolean processPayment(Payment payment, int bookingId) {
        validatePayment(payment);
        validatePositiveId(bookingId, "Booking ID");

        Booking booking = bookingDAO.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Booking with ID " + bookingId
                                + " does not exist."
                ));

        if (!booking.isConfirmed()) {
            throw new IllegalStateException(
                    "Payment cannot be processed for an inactive booking."
            );
        }

        boolean processed = payment.processPayment();
        boolean saved = paymentDAO.add(payment, bookingId);

        if (!saved) {
            throw new IllegalStateException(
                    "Unable to save the payment record."
            );
        }

        return processed;
    }


    public boolean updatePayment(Payment payment) {
        validatePayment(payment);

        if (paymentDAO.findById(payment.getPaymentId()).isEmpty()) {
            throw new IllegalArgumentException(
                    "Payment with ID " + payment.getPaymentId()
                            + " does not exist."
            );
        }

        if (!payment.validatePaymentDetails()) {
            throw new IllegalArgumentException(
                    "Invalid payment details."
            );
        }

        return paymentDAO.update(payment);
    }

    public Optional<Payment> findPaymentById(int paymentId) {
        validatePositiveId(paymentId, "Payment ID");

        return paymentDAO.findById(paymentId);
    }

    public String verifyPaymentStatus(int paymentId) {
        Payment payment = findPaymentById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment with ID " + paymentId
                                + " does not exist."
                ));

        return payment.verifyPaymentStatus();
    }

    public List<Payment> findPaymentsByBookingId(int bookingId) {
        validatePositiveId(bookingId, "Booking ID");

        if (bookingDAO.findById(bookingId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Booking with ID " + bookingId
                            + " does not exist."
            );
        }

        return paymentDAO.findByBookingId(bookingId);
    }

    public List<Payment> getAllPayments() {
        return paymentDAO.findAll();
    }

    public boolean refundPayment(int paymentId) {
        validatePositiveId(paymentId, "Payment ID");

        Payment payment = paymentDAO.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment with ID " + paymentId
                                + " does not exist."
                ));

        if (!payment.refundPayment(payment.getAmount())) {
            return false;
        }

        return paymentDAO.update(payment);
    }

    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException(
                    "Payment cannot be null."
            );
        }
    }

    private void validatePositiveId(int id, String fieldName) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero."
            );
        }
    }
}