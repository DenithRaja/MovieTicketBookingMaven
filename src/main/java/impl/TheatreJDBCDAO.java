package impl;

import dao.TheatreDAO;
import model.Theatre;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TheatreJDBCDAO implements TheatreDAO {


    @Override
    public boolean add(Theatre theatre) {
        if (!isValidTheatre(theatre)) {
            return false;
        }

        String sql = """
            INSERT INTO theatres (theatre_id, name, location)
            VALUES (?, ?, ?)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, theatre.getTheatreId());
            statement.setString(2, theatre.getName().trim());
            statement.setString(3, theatre.getLocation().trim());

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            if ("23505".equals(exception.getSQLState())) {
                return false;
            }

            System.err.println(
                    "Database error while adding theatre: "
                            + exception.getMessage()
            );

            return false;
        }
    }

    @Override
    public boolean update(Theatre theatre) {
        if (!isValidTheatre(theatre)
                || theatre.getTheatreId() <= 0) {
            return false;
        }

        String sql = """
            UPDATE theatres
            SET name = ?, location = ?
            WHERE theatre_id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, theatre.getName().trim());
            statement.setString(2, theatre.getLocation().trim());
            statement.setInt(3, theatre.getTheatreId());

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            if ("23505".equals(exception.getSQLState())) {
                return false;
            }

            System.err.println(
                    "Database error while updating theatre: "
                            + exception.getMessage()
            );

            return false;
        }
    }


    @Override
    public boolean deleteById(int theatreId) {
        if (theatreId <= 0) {
            return false;
        }

        String sql = "DELETE FROM theatres WHERE theatre_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, theatreId);

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {

            if ("23503".equals(exception.getSQLState())) {
                return false;
            }

            System.err.println(
                    "Database error while deleting theatre: "
                            + exception.getMessage()
            );

            return false;
        }
    }


    @Override
    public Optional<Theatre> findById(int theatreId) {
        if (theatreId <= 0) {
            return Optional.empty();
        }

        String sql = """
                SELECT theatre_id, name, location
                FROM theatres
                WHERE theatre_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, theatreId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapTheatre(resultSet));
                }
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to find theatre with ID: " + theatreId,
                    exception
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Theatre> findAll() {
        String sql = """
                SELECT theatre_id, name, location
                FROM theatres
                ORDER BY theatre_id
                """;

        List<Theatre> theatres = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                theatres.add(mapTheatre(resultSet));
            }

            return theatres;

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to retrieve theatres.",
                    exception
            );
        }
    }

    private boolean isValidTheatre(Theatre theatre) {
        return theatre != null
                && theatre.getTheatreId() > 0
                && theatre.getName() != null
                && !theatre.getName().isBlank()
                && theatre.getLocation() != null
                && !theatre.getLocation().isBlank();
    }

    private Theatre mapTheatre(ResultSet resultSet)
            throws SQLException {

        return new Theatre(
                resultSet.getInt("theatre_id"),
                resultSet.getString("name"),
                resultSet.getString("location")
        );
    }
}