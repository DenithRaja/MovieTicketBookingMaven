package impl;

import dao.MovieDAO;
import model.Movie;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovieJDBCDAO implements MovieDAO {


    @Override
    public boolean add(Movie movie) {
        if (!isValidMovie(movie)) {
            return false;
        }

        String sql = """
            INSERT INTO movies
            (movie_id, title, genre, language, duration)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, movie.getMovieId());
            statement.setString(2, movie.getTitle().trim());
            statement.setString(3, movie.getGenre().trim());
            statement.setString(4, movie.getLanguage().trim());
            statement.setInt(5, movie.getDuration());

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {

            if ("23505".equals(exception.getSQLState())) {
                return false;
            }

            System.err.println(
                    "Database error while adding movie: "
                            + exception.getMessage()
            );

            return false;
        }
    }



    @Override
    public boolean update(Movie movie) {
        if (!isValidMovie(movie)
                || movie.getMovieId() <= 0) {
            return false;
        }

        String sql = """
            UPDATE movies
            SET title = ?,
                genre = ?,
                language = ?,
                duration = ?
            WHERE movie_id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, movie.getTitle().trim());
            statement.setString(2, movie.getGenre().trim());
            statement.setString(3, movie.getLanguage().trim());
            statement.setInt(4, movie.getDuration());
            statement.setInt(5, movie.getMovieId());

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            if ("23505".equals(exception.getSQLState())) {
                return false;
            }

            System.err.println(
                    "Database error while updating movie: "
                            + exception.getMessage()
            );

            return false;
        }
    }



    @Override
    public boolean deleteById(int movieId) {
        if (movieId <= 0) {
            return false;
        }

        String sql = "DELETE FROM movies WHERE movie_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, movieId);

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            if ("23503".equals(exception.getSQLState())) {
                return false;
            }

            System.err.println(
                    "Database error while deleting movie: "
                            + exception.getMessage()
            );

            return false;
        }
    }


    @Override
    public Optional<Movie> findById(int movieId) {
        if (movieId <= 0) {
            return Optional.empty();
        }

        String sql = """
                SELECT movie_id, title, genre, language, duration
                FROM movies
                WHERE movie_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, movieId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapMovie(resultSet));
                }
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to find movie with ID: " + movieId,
                    exception
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Movie> findAll() {
        String sql = """
                SELECT movie_id, title, genre, language, duration
                FROM movies
                ORDER BY movie_id
                """;

        List<Movie> movies = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                movies.add(mapMovie(resultSet));
            }

            return movies;

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to retrieve movies.",
                    exception
            );
        }
    }

    @Override
    public List<Movie> searchByTitle(String title) {
        if (title == null || title.isBlank()) {
            return new ArrayList<>();
        }

        String sql = """
                SELECT movie_id, title, genre, language, duration
                FROM movies
                WHERE title ILIKE ?
                ORDER BY title
                """;

        List<Movie> movies = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, "%" + title.trim() + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    movies.add(mapMovie(resultSet));
                }
            }

            return movies;

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to search movies by title.",
                    exception
            );
        }
    }

    private boolean isValidMovie(Movie movie) {
        return movie != null
                && movie.getMovieId() > 0
                && movie.getTitle() != null
                && !movie.getTitle().isBlank()
                && movie.getGenre() != null
                && !movie.getGenre().isBlank()
                && movie.getLanguage() != null
                && !movie.getLanguage().isBlank()
                && movie.getDuration() > 0;
    }

    private Movie mapMovie(ResultSet resultSet)
            throws SQLException {

        return new Movie(
                resultSet.getInt("movie_id"),
                resultSet.getString("title"),
                resultSet.getString("genre"),
                resultSet.getString("language"),
                resultSet.getInt("duration")
        );
    }
}