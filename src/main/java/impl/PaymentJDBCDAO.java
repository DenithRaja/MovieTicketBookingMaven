package impl;

import dao.PaymentDAO;
import model.Payment;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentJDBCDAO implements PaymentDAO {

    @Override
    public boolean add(Payment payment, int bookingId) {
        if (payment == null || bookingId <= 0) {
            return false;
        }

        String sql = """
            INSERT INTO payments
            (amount, payment_method, status, refund_amount, booking_id)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setDouble(1, payment.getAmount());
            statement.setString(2, payment.getPaymentMethod());
            statement.setString(3, payment.getStatus());
            statement.setDouble(4, payment.getRefundAmount());
            statement.setInt(5, bookingId);

            if (statement.executeUpdate() != 1) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payment.setPaymentId(generatedKeys.getInt(1));
                    return true;
                }
            }

            throw new IllegalStateException(
                    "Payment was inserted, but its generated ID was not returned."
            );

        } catch (SQLException exception) {

            if (exception.getSQLState() != null
                    && exception.getSQLState().startsWith("23")) {
                return false;
            }

            System.err.println(
                    "Database error while adding payment: "
                            + exception.getMessage()
            );

            return false;
        }
    }

    @Override
    public boolean update(Payment payment) {
        String sql = """
            UPDATE payments
            SET amount = ?, payment_method = ?, status = ?,
                refund_amount = ?
            WHERE payment_id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setDouble(1, payment.getAmount());
            statement.setString(2, payment.getPaymentMethod());
            statement.setString(3, payment.getStatus());
            statement.setDouble(4, payment.getRefundAmount());
            statement.setInt(5, payment.getPaymentId());

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            if (exception.getSQLState() != null
                    && exception.getSQLState().startsWith("23")) {
                return false;
            }

            System.err.println(
                    "Database error while updating payment: "
                            + exception.getMessage()
            );

            return false;
        }
    }

    @Override
    public Optional<Payment> findById(int paymentId) {
        String sql = """
            SELECT payment_id, amount, payment_method, status,
                   refund_amount
            FROM payments
            WHERE payment_id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, paymentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapPayment(resultSet));
                }
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to find payment by ID: " + paymentId,
                    exception
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Payment> findByBookingId(int bookingId) {
        String sql = """
            SELECT payment_id, amount, payment_method, status,
                   refund_amount
            FROM payments
            WHERE booking_id = ?
            ORDER BY payment_id
            """;

        List<Payment> payments = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    payments.add(mapPayment(resultSet));
                }
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to retrieve payments for booking ID: "
                            + bookingId,
                    exception
            );
        }

        return payments;
    }

    @Override
    public List<Payment> findAll() {
        String sql = """
            SELECT payment_id, amount, payment_method, status,
                   refund_amount
            FROM payments
            ORDER BY payment_id
            """;

        List<Payment> payments = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                payments.add(mapPayment(resultSet));
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to retrieve all payments.",
                    exception
            );
        }

        return payments;
    }

    private Payment mapPayment(ResultSet resultSet)
            throws SQLException {

        Payment payment = new Payment(
                resultSet.getInt("payment_id"),
                resultSet.getDouble("amount"),
                resultSet.getString("payment_method"),
                resultSet.getString("status")
        );

        payment.setRefundAmount(
                resultSet.getDouble("refund_amount")
        );

        return payment;
    }
}