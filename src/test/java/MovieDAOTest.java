
package dao;

import model.Movie;
import org.junit.jupiter.api.Test;
import storage.MovieDataStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieDAOTest {

    @Test
    void checkAddMovie() {
        MovieDataStorage storage = new MovieDataStorage();
        MovieDAO movieDAO = new MovieDataStorageDAO(storage);

        Movie movie = new Movie(1, "Inception", "Sci-Fi", "English", 148);

        boolean success = movieDAO.add(movie);

        assertTrue(success);
        assertTrue(storage.movies.containsKey(1));
        assertEquals(movie, storage.movies.get(1));
    }

    @Test
    void checkFindMovieById() {
        MovieDataStorage storage = new MovieDataStorage();
        MovieDAO movieDAO = new MovieDataStorageDAO(storage);

        Movie movie = new Movie(1, "Inception", "Sci-Fi", "English", 148);

        movieDAO.add(movie);

        assertTrue(movieDAO.findById(1).isPresent());
        assertEquals(movie, movieDAO.findById(1).get());
    }

    @Test
    void checkFindAllMovies() {
        MovieDataStorage storage = new MovieDataStorage();
        MovieDAO movieDAO = new MovieDataStorageDAO(storage);

        Movie movie1 = new Movie(1, "Inception", "Sci-Fi", "English", 148);

        Movie movie2 = new Movie(2, "Interstellar", "Sci-Fi", "English", 169);

        movieDAO.add(movie1);
        movieDAO.add(movie2);

        List<Movie> movies = movieDAO.findAll();

        assertEquals(2, movies.size());
        assertTrue(movies.contains(movie1));
        assertTrue(movies.contains(movie2));
    }

    @Test
    void checkSearchMovieByTitle() {
        MovieDataStorage storage = new MovieDataStorage();
        MovieDAO movieDAO = new MovieDataStorageDAO(storage);

        Movie movie1 = new Movie(1, "Inception", "Sci-Fi", "English", 148);

        Movie movie2 = new Movie(2, "Interstellar", "Sci-Fi", "English", 169);

        movieDAO.add(movie1);
        movieDAO.add(movie2);

        List<Movie> results = movieDAO.searchByTitle("inception");

        assertEquals(1, results.size());
        assertTrue(results.contains(movie1));
    }

    @Test
    void checkUpdateMovie() {
        MovieDataStorage storage = new MovieDataStorage();
        MovieDAO movieDAO = new MovieDataStorageDAO(storage);

        Movie originalMovie = new Movie(1, "Inception", "Sci-Fi", "English", 148);

        movieDAO.add(originalMovie);

        Movie updatedMovie = new Movie(1, "Inception Extended Cut", "Sci-Fi", "English", 150);

        boolean success = movieDAO.update(updatedMovie);

        Movie result = movieDAO.findById(1).get();

        assertTrue(success);
        assertEquals("Inception Extended Cut", result.getTitle());
        assertEquals(150, result.getDuration());
    }

    @Test
    void checkDeleteMovieById() {
        MovieDataStorage storage = new MovieDataStorage();
        MovieDAO movieDAO = new MovieDataStorageDAO(storage);

        Movie movie = new Movie(1, "Inception", "Sci-Fi", "English", 148);

        movieDAO.add(movie);

        boolean success = movieDAO.deleteById(1);

        assertTrue(success);
        assertFalse(storage.movies.containsKey(1));
    }
}
