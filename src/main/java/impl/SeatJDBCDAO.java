package impl;

import dao.SeatDAO;
import model.Seat;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeatJDBCDAO implements SeatDAO {


    @Override
    public boolean add(Seat seat, int screenId) {
        if (!isValidSeat(seat) || screenId <= 0) {
            return false;
        }

        String sql = """
            INSERT INTO seats
            (seat_id, seat_number, seat_row, seat_type, screen_id)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, seat.getSeatId());
            statement.setString(2, seat.getSeatNumber().trim());
            statement.setString(3, seat.getSeatRow().trim());
            statement.setString(4, seat.getSeatType().trim());
            statement.setInt(5, screenId);

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            if ("23505".equals(exception.getSQLState())) {
                return false;
            }
            System.err.println(
                    "Database error while adding seat: "
                            + exception.getMessage()
            );

            return false;
        }
    }


    @Override
    public boolean update(Seat seat) {
        if (!isValidSeat(seat) || seat.getSeatId() <= 0) {
            return false;
        }

        String sql = """
            UPDATE seats
            SET seat_number = ?,
                seat_row = ?,
                seat_type = ?
            WHERE seat_id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, seat.getSeatNumber().trim());
            statement.setString(2, seat.getSeatRow().trim());
            statement.setString(3, seat.getSeatType().trim());
            statement.setInt(4, seat.getSeatId());

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            if ("23505".equals(exception.getSQLState())) {
                return false;
            }

            System.err.println(
                    "Database error while updating seat: "
                            + exception.getMessage()
            );

            return false;
        }
    }

    @Override
    public boolean deleteById(int seatId) {
        if (seatId <= 0) {
            return false;
        }

        String sql = "DELETE FROM seats WHERE seat_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, seatId);

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            if ("23503".equals(exception.getSQLState())) {
                return false;
            }

            System.err.println(
                    "Database error while deleting seat: "
                            + exception.getMessage()
            );

            return false;
        }
    }



    @Override
    public Optional<Seat> findById(int seatId) {
        if (seatId <= 0) {
            return Optional.empty();
        }

        String sql = """
            SELECT seat_id, seat_number, seat_row, seat_type
            FROM seats
            WHERE seat_id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, seatId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapSeat(resultSet));
                }
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to find seat by ID: " + seatId,
                    exception
            );
        }

        return Optional.empty();
    }



    @Override
    public List<Seat> findByScreenId(int screenId) {
        if (screenId <= 0) {
            return new ArrayList<>();
        }

        String sql = """
            SELECT seat_id, seat_number, seat_row, seat_type
            FROM seats
            WHERE screen_id = ?
            ORDER BY seat_row, seat_number
            """;

        List<Seat> seats = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, screenId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    seats.add(mapSeat(resultSet));
                }
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to retrieve seats for screen ID: "
                            + screenId,
                    exception
            );
        }

        return seats;
    }


    private boolean isValidSeat(Seat seat) {
        return seat != null
                && seat.getSeatId() > 0
                && seat.getSeatNumber() != null
                && !seat.getSeatNumber().isBlank()
                && seat.getSeatRow() != null
                && !seat.getSeatRow().isBlank()
                && seat.getSeatType() != null
                && !seat.getSeatType().isBlank();
    }

    private Seat mapSeat(ResultSet resultSet) throws SQLException {
        return new Seat(
                resultSet.getInt("seat_id"),
                resultSet.getString("seat_number"),
                resultSet.getString("seat_row"),
                resultSet.getString("seat_type")
        );
    }
}