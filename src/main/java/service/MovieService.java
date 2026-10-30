package service;

import dao.MovieDAO;
import dao.ShowDAO;
import model.Movie;
import model.Show;

import java.util.List;
import java.util.Optional;

public class MovieService {

    private final MovieDAO movieDAO;
    private final ShowDAO showDAO;

    public MovieService(MovieDAO movieDAO, ShowDAO showDAO) {
        if (movieDAO == null || showDAO == null) {
            throw new IllegalArgumentException(
                    "DAO dependencies cannot be null."
            );
        }

        this.movieDAO = movieDAO;
        this.showDAO = showDAO;
    }

    public boolean addMovie(Movie movie) {
        validateMovieDetails(movie);

        if (movie.getMovieId() <= 0) {
            throw new IllegalArgumentException(
                    "Movie ID must be greater than zero."
            );
        }

        return movieDAO.add(movie);
    }

    public boolean updateMovie(Movie movie) {
        validateMovieDetails(movie);
        validateMovieId(movie.getMovieId());

        if (movieDAO.findById(movie.getMovieId()).isEmpty()) {
            throw new IllegalArgumentException(
                    "Movie with ID " + movie.getMovieId()
                            + " does not exist."
            );
        }

        return movieDAO.update(movie);
    }

    public boolean removeMovie(int movieId) {
        validateMovieId(movieId);

        if (movieDAO.findById(movieId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Movie with ID " + movieId
                            + " does not exist."
            );
        }

        List<Show> shows = showDAO.findByMovieId(movieId);

        if (!shows.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete this movie because scheduled shows "
                            + "depend on it."
            );
        }

        return movieDAO.deleteById(movieId);
    }

    public Optional<Movie> findMovieById(int movieId) {
        validateMovieId(movieId);
        return movieDAO.findById(movieId);
    }

    public List<Movie> getAllMovies() {
        return movieDAO.findAll();
    }

    public List<Movie> searchMoviesByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Search keyword cannot be empty."
            );
        }

        return movieDAO.searchByTitle(title.trim());
    }

    private void validateMovieDetails(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException(
                    "Movie information cannot be null."
            );
        }

        if (movie.getTitle() == null
                || movie.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Movie title cannot be empty."
            );
        }

        if (movie.getGenre() == null
                || movie.getGenre().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Movie genre cannot be empty."
            );
        }

        if (movie.getLanguage() == null
                || movie.getLanguage().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Movie language cannot be empty."
            );
        }

        if (movie.getDuration() <= 0) {
            throw new IllegalArgumentException(
                    "Movie duration must be greater than zero minutes."
            );
        }
    }

    private void validateMovieId(int movieId) {
        if (movieId <= 0) {
            throw new IllegalArgumentException(
                    "Movie ID must be greater than zero."
            );
        }
    }
}