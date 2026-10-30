
package impl;

import dao.BookingDAO;
import dao.PaymentDAO;
import model.Payment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class PaymentDataStorageDAO implements PaymentDAO {

    private final Map<Integer, Payment> payments = new LinkedHashMap<>();
    private final Map<Integer, Integer> paymentBookingIds = new LinkedHashMap<>();

    private final AtomicInteger paymentIdSequence =
            new AtomicInteger(0);

    private final Object storageLock = new Object();

    private final BookingDAO bookingDAO;

    public PaymentDataStorageDAO(BookingDAO bookingDAO) {
        if (bookingDAO == null) {
            throw new IllegalArgumentException(
                    "BookingDAO cannot be null."
            );
        }

        this.bookingDAO = bookingDAO;
    }

    @Override
    public boolean add(Payment payment, int bookingId) {

        if (payment == null) {
            throw new IllegalArgumentException(
                    "Payment cannot be null."
            );
        }

        if (bookingId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid booking ID."
            );
        }

        if (bookingDAO.findById(bookingId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Booking not found."
            );
        }

        if (!payment.validatePaymentDetails()) {
            throw new IllegalArgumentException(
                    "Invalid payment details."
            );
        }

        synchronized (storageLock) {

            int paymentId = paymentIdSequence.incrementAndGet();

            payment.setPaymentId(paymentId);

            payments.put(paymentId, payment);
            paymentBookingIds.put(paymentId, bookingId);

            return true;
        }
    }

    @Override
    public boolean update(Payment payment) {

        if (payment == null) {
            throw new IllegalArgumentException(
                    "Payment cannot be null."
            );
        }

        int paymentId = payment.getPaymentId();

        if (paymentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid payment ID."
            );
        }

        if (!payment.validatePaymentDetails()) {
            throw new IllegalArgumentException(
                    "Invalid payment details."
            );
        }

        synchronized (storageLock) {

            if (!payments.containsKey(paymentId)) {
                throw new IllegalArgumentException(
                        "Payment not found."
                );
            }

            payments.put(paymentId, payment);

            return true;
        }
    }

    @Override
    public Optional<Payment> findById(int paymentId) {

        if (paymentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid payment ID."
            );
        }

        synchronized (storageLock) {
            return Optional.ofNullable(payments.get(paymentId));
        }
    }

    @Override
    public List<Payment> findByBookingId(int bookingId) {

        if (bookingId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid booking ID."
            );
        }

        if (bookingDAO.findById(bookingId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Booking not found."
            );
        }

        List<Payment> matchingPayments = new ArrayList<>();

        synchronized (storageLock) {

            for (Map.Entry<Integer, Integer> entry :
                    paymentBookingIds.entrySet()) {

                if (entry.getValue() == bookingId) {

                    Payment payment = payments.get(entry.getKey());

                    if (payment != null) {
                        matchingPayments.add(payment);
                    }
                }
            }
        }

        return matchingPayments;
    }

    @Override
    public List<Payment> findAll() {

        synchronized (storageLock) {
            return new ArrayList<>(payments.values());
        }
    }
}
