package dao;

import model.Movie;
import storage.MovieDataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovieDataStorageDAO implements MovieDAO {
    private final MovieDataStorage storage;

    public MovieDataStorageDAO(MovieDataStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean add(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null");
        }

        int movieId = movie.getMovieId();
        if (storage.movies.containsKey(movieId)) {
            throw new IllegalArgumentException("Movie ID already exists");
        }

        storage.movies.put(movieId, movie);
        return true;
    }

    @Override
    public boolean update(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null");
        }

        int movieId = movie.getMovieId();
        if (!storage.movies.containsKey(movieId)) {
            throw new IllegalArgumentException("Movie not found");
        }

        storage.movies.put(movieId, movie);
        return true;
    }

    @Override
    public boolean deleteById(int movieId) {
        if (storage.movies.remove(movieId) == null) {
            throw new IllegalArgumentException("Movie not found");
        }
        return true;
    }

    @Override
    public Optional<Movie> findById(int movieId) {
        Movie movie = storage.movies.get(movieId);
        if (movie == null) {
            return Optional.empty();
        }
        return Optional.of(movie);
    }

    @Override
    public List<Movie> findAll() {
        return new ArrayList<>(storage.movies.values());
    }

    @Override
    public List<Movie> searchByTitle(String title) {
        List<Movie> matchingMovies = new ArrayList<>();
        for (Movie movie : storage.movies.values()) {
            if (movie.getTitle().toLowerCase().contains(title.toLowerCase())) {
                matchingMovies.add(movie);
            }
        }
        return matchingMovies;
    }
}