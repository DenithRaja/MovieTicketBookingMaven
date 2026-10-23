package dao;

import model.Payment;
import storage.MovieDataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentDataStorageDAO implements PaymentDAO {
    private final MovieDataStorage storage;

    public PaymentDataStorageDAO(MovieDataStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean add(Payment payment, int bookingId) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }
        if (bookingId <= 0) {
            throw new IllegalArgumentException("Invalid booking ID");
        }
        if (!storage.bookings.containsKey(bookingId)) {
            throw new IllegalArgumentException("Booking not found");
        }

        int paymentId = payment.getPaymentId();
        if (paymentId <= 0) {
            throw new IllegalArgumentException("Invalid payment ID");
        }
        if (storage.payments.containsKey(paymentId)) {
            throw new IllegalArgumentException("Payment ID already exists");
        }
        if (!payment.validatePaymentDetails()) {
            throw new IllegalArgumentException("Invalid payment details");
        }

        storage.payments.put(paymentId, payment);
        storage.paymentBookingIds.put(paymentId, bookingId);
        return true;
    }

    @Override
    public boolean update(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }

        int paymentId = payment.getPaymentId();
        if (paymentId <= 0) {
            throw new IllegalArgumentException("Invalid payment ID");
        }
        if (!storage.payments.containsKey(paymentId)) {
            throw new IllegalArgumentException("Payment not found");
        }
        if (!payment.validatePaymentDetails()) {
            throw new IllegalArgumentException("Invalid payment details");
        }

        storage.payments.put(paymentId, payment);
        return true;
    }

    @Override
    public Optional<Payment> findById(int paymentId) {
        if (paymentId <= 0) {
            throw new IllegalArgumentException("Invalid payment ID");
        }
        return Optional.ofNullable(storage.payments.get(paymentId));
    }

    @Override
    public List<Payment> findByBookingId(int bookingId) {
        if (bookingId <= 0) {
            throw new IllegalArgumentException("Invalid booking ID");
        }
        if (!storage.bookings.containsKey(bookingId)) {
            throw new IllegalArgumentException("Booking not found");
        }

        List<Payment> payments = new ArrayList<>();
        for (Payment payment : storage.payments.values()) {
            Integer storedBookingId = storage.paymentBookingIds.get(payment.getPaymentId());
            if (storedBookingId != null && storedBookingId == bookingId) {
                payments.add(payment);
            }
        }
        return payments;
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(storage.payments.values());
    }
}