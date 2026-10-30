package impl;

import dao.ShowDAO;
import model.Screen;
import model.Show;
import util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShowJDBCDAO implements ShowDAO {


    @Override
    public boolean add(Show show, int movieId, int screenId) {
        String sql = """
            INSERT INTO shows
            (show_id, show_date, show_time, base_ticket_price,
             movie_id, screen_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, show.getShowId());
            statement.setDate(2, Date.valueOf(show.getShowDate()));
            statement.setTime(3, Time.valueOf(show.getShowTime()));
            statement.setDouble(4, show.getBaseTicketPrice());
            statement.setInt(5, movieId);
            statement.setInt(6, screenId);

            boolean added = statement.executeUpdate() == 1;

            if (added) {
                show.setMovieId(movieId);
            }

            return added;

        } catch (SQLException exception) {
            if (exception.getSQLState() != null
                    && exception.getSQLState().startsWith("23")) {
                return false;
            }

            System.err.println(
                    "Database error while adding show: "
                            + exception.getMessage()
            );

            return false;
        }
    }


    @Override
    public boolean update(Show show) {
        if (show == null
                || show.getShowId() <= 0
                || show.getShowDate() == null
                || show.getShowTime() == null
                || show.getScreen() == null
                || show.getMovieId() <= 0
                || show.getScreen().getScreenId() <= 0
                || !Double.isFinite(show.getBaseTicketPrice())
                || show.getBaseTicketPrice() < 0) {
            return false;
        }

        String sql = """
        UPDATE shows
        SET show_date = ?,
            show_time = ?,
            base_ticket_price = ?,
            movie_id = ?,
            screen_id = ?
        WHERE show_id = ?
        """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(show.getShowDate()));
            statement.setTime(2, Time.valueOf(show.getShowTime()));
            statement.setDouble(3, show.getBaseTicketPrice());
            statement.setInt(4, show.getMovieId());
            statement.setInt(5, show.getScreen().getScreenId());
            statement.setInt(6, show.getShowId());

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            if (exception.getSQLState() != null
                    && exception.getSQLState().startsWith("23")) {
                return false;
            }

            System.err.println(
                    "Database error while updating show: "
                            + exception.getMessage()
            );

            return false;
        }
    }

    @Override
    public boolean deleteById(int showId) {
        String sql = "DELETE FROM shows WHERE show_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, showId);

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            if ("23503".equals(exception.getSQLState())) {
                return false;
            }

            System.err.println(
                    "Database error while deleting show: "
                            + exception.getMessage()
            );

            return false;
        }
    }

    @Override
    public Optional<Show> findById(int showId) {
        String sql = """
            SELECT show_id, show_date, show_time, base_ticket_price,
                   movie_id, screen_id
            FROM shows
            WHERE show_id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, showId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapShow(connection, resultSet));
                }
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to find show by ID: " + showId,
                    exception
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Show> findByMovieId(int movieId) {
        String sql = """
                SELECT show_id, show_date, show_time, base_ticket_price,
                       movie_id, screen_id
                FROM shows
                WHERE movie_id = ?
                ORDER BY show_date, show_time
                """;

        return findShowsByForeignKey(sql, movieId);
    }

    @Override
    public List<Show> findByScreenId(int screenId) {
        String sql = """
                SELECT show_id, show_date, show_time, base_ticket_price,
                       movie_id, screen_id
                FROM shows
                WHERE screen_id = ?
                ORDER BY show_date, show_time
                """;

        return findShowsByForeignKey(sql, screenId);
    }

    @Override
    public List<Show> findAll() {
        String sql = """
            SELECT show_id, show_date, show_time, base_ticket_price,
                   movie_id, screen_id
            FROM shows
            ORDER BY show_date, show_time
            """;

        List<Show> shows = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                shows.add(mapShow(connection, resultSet));
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to retrieve all shows.",
                    exception
            );
        }

        return shows;
    }

    private List<Show> findShowsByForeignKey(String sql, int id) {
        List<Show> shows = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    shows.add(mapShow(connection, resultSet));
                }
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to retrieve shows using ID: " + id,
                    exception
            );
        }

        return shows;
    }

    private Show mapShow(Connection connection, ResultSet resultSet)
            throws SQLException {

        int screenId = resultSet.getInt("screen_id");

        String screenSql = """
                SELECT screen_id, screen_name, capacity, theatre_id
                FROM screens
                WHERE screen_id = ?
                """;

        Screen screen;

        try (PreparedStatement statement =
                     connection.prepareStatement(screenSql)) {

            statement.setInt(1, screenId);

            try (ResultSet screenResult = statement.executeQuery()) {
                if (!screenResult.next()) {
                    throw new SQLException(
                            "Screen not found for show: "
                                    + resultSet.getInt("show_id")
                    );
                }

                screen = new Screen(
                        screenResult.getInt("screen_id"),
                        screenResult.getString("screen_name"),
                        screenResult.getInt("capacity")
                );

                screen.setTheatreId(screenResult.getInt("theatre_id"));
            }
        }

        Show show = new Show(
                resultSet.getInt("show_id"),
                resultSet.getDate("show_date").toLocalDate(),
                resultSet.getTime("show_time").toLocalTime(),
                resultSet.getDouble("base_ticket_price"),
                screen
        );

        show.setMovieId(resultSet.getInt("movie_id"));

        return show;
    }
}