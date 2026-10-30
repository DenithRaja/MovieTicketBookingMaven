package impl;

import dao.ScreenDAO;
import model.Screen;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScreenJDBCDAO implements ScreenDAO {


    @Override
    public boolean add(Screen screen, int theatreId) {
        if (!isValidScreen(screen) || theatreId <= 0) {
            return false;
        }

        String sql = """
            INSERT INTO screens
            (screen_id, screen_name, capacity, theatre_id)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, screen.getScreenId());
            statement.setString(2, screen.getScreenName().trim());
            statement.setInt(3, screen.getCapacity());
            statement.setInt(4, theatreId);

            boolean added = statement.executeUpdate() == 1;

            if (added) {
                screen.setTheatreId(theatreId);
            }

            return added;

        } catch (SQLException exception) {
            if ("23505".equals(exception.getSQLState())) {
                return false;
            }

            System.err.println(
                    "Database error while adding screen: "
                            + exception.getMessage()
            );

            return false;
        }
    }



    @Override
    public boolean update(Screen screen) {
        if (!isValidScreen(screen)
                || screen.getScreenId() <= 0) {
            return false;
        }

        String sql = """
            UPDATE screens
            SET screen_name = ?, capacity = ?
            WHERE screen_id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, screen.getScreenName().trim());
            statement.setInt(2, screen.getCapacity());
            statement.setInt(3, screen.getScreenId());

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            if ("23505".equals(exception.getSQLState())) {
                return false;
            }

            System.err.println(
                    "Database error while updating screen: "
                            + exception.getMessage()
            );

            return false;
        }
    }



    @Override
    public boolean deleteById(int screenId) {
        if (screenId <= 0) {
            return false;
        }

        String sql = "DELETE FROM screens WHERE screen_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, screenId);

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            if ("23503".equals(exception.getSQLState())) {
                return false;
            }

            System.err.println(
                    "Database error while deleting screen: "
                            + exception.getMessage()
            );

            return false;
        }
    }


    @Override
    public Optional<Screen> findById(int screenId) {
        if (screenId <= 0) {
            return Optional.empty();
        }

        String sql = """
                SELECT screen_id, screen_name, capacity, theatre_id
                FROM screens
                WHERE screen_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, screenId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapScreen(resultSet));
                }
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to find screen with ID: " + screenId,
                    exception
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Screen> findByTheatreId(int theatreId) {
        if (theatreId <= 0) {
            return new ArrayList<>();
        }

        String sql = """
                SELECT screen_id, screen_name, capacity, theatre_id
                FROM screens
                WHERE theatre_id = ?
                ORDER BY screen_id
                """;

        List<Screen> screens = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, theatreId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    screens.add(mapScreen(resultSet));
                }
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to retrieve screens for theatre ID: "
                            + theatreId,
                    exception
            );
        }

        return screens;
    }

    private boolean isValidScreen(Screen screen) {
        return screen != null
                && screen.getScreenId() > 0
                && screen.getScreenName() != null
                && !screen.getScreenName().isBlank()
                && screen.getCapacity() > 0;
    }

    private Screen mapScreen(ResultSet resultSet)
            throws SQLException {

        Screen screen = new Screen(
                resultSet.getInt("screen_id"),
                resultSet.getString("screen_name"),
                resultSet.getInt("capacity")
        );

        screen.setTheatreId(resultSet.getInt("theatre_id"));

        return screen;
    }
}