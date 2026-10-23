package dao;

import model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentDAO {
    boolean add(Payment payment, int bookingId);
    boolean update(Payment payment);
    Optional<Payment> findById(int paymentId);
    List<Payment> findByBookingId(int bookingId);
    List<Payment> findAll();
}