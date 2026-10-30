package impl;

import dao.MovieDAO;
import model.Movie;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class MovieDataStorageDAO implements MovieDAO {

    private final Map<Integer, Movie> movies = new LinkedHashMap<>();

    @Override
    public boolean add(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null");
        }

        int movieId = movie.getMovieId();

        if (movies.containsKey(movieId)) {
            throw new IllegalArgumentException("Movie ID already exists");
        }

        movies.put(movieId, movie);
        return true;
    }

    @Override
    public boolean update(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null");
        }

        int movieId = movie.getMovieId();

        if (!movies.containsKey(movieId)) {
            throw new IllegalArgumentException("Movie not found");
        }

        movies.put(movieId, movie);
        return true;
    }

    @Override
    public boolean deleteById(int movieId) {
        if (movies.remove(movieId) == null) {
            throw new IllegalArgumentException("Movie not found");
        }

        return true;
    }

    @Override
    public Optional<Movie> findById(int movieId) {
        return Optional.ofNullable(movies.get(movieId));
    }

    @Override
    public List<Movie> findAll() {
        return new ArrayList<>(movies.values());
    }

    @Override
    public List<Movie> searchByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Search title cannot be null or blank"
            );
        }

        String searchTitle = title.toLowerCase(Locale.ROOT);
        List<Movie> matchingMovies = new ArrayList<>();

        for (Movie movie : movies.values()) {
            String movieTitle = movie.getTitle().toLowerCase(Locale.ROOT);

            if (movieTitle.contains(searchTitle)) {
                matchingMovies.add(movie);
            }
        }

        return matchingMovies;
    }
}