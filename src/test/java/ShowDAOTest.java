package dao;

import model.Movie;
import model.Screen;
import model.Show;
import org.junit.jupiter.api.Test;
import storage.MovieDataStorage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ShowDAOTest {

    @Test
    void checkAddShow() {

        MovieDataStorage storage = new MovieDataStorage();
        ShowDAO showDAO = new ShowDataStorageDAO(storage);

        Movie movie = new Movie(1, "Inception", "Sci-Fi", "English", 148);
        storage.movies.put(movie.getMovieId(), movie);

        Screen screen = new Screen(1, "Screen 1", 100);
        storage.screens.put(screen.getScreenId(), screen);

        Show show = new Show(
                1, LocalDate.of(2026, 11, 10),
                LocalTime.of(18, 30), 200.0, null
        );

        boolean success = showDAO.add(show, 1, 1);

        assertTrue(success);
        assertTrue(storage.shows.containsKey(1));
        assertEquals(show, storage.shows.get(1));
        assertEquals(1, show.getMovieId());
        assertEquals(screen, show.getScreen());
    }

    @Test
    void checkFindShowById() {

        MovieDataStorage storage = new MovieDataStorage();
        ShowDAO showDAO = new ShowDataStorageDAO(storage);

        Movie movie = new Movie(1, "Inception", "Sci-Fi", "English", 148);
        storage.movies.put(movie.getMovieId(), movie);

        Screen screen = new Screen(1, "Screen 1", 100);
        storage.screens.put(screen.getScreenId(), screen);

        Show show = new Show(
                1, LocalDate.of(2026, 11, 10),
                LocalTime.of(18, 30), 200.0, null
        );
        showDAO.add(show, 1, 1);

        Optional<Show> result = showDAO.findById(1);

        assertTrue(result.isPresent());
        assertEquals(show, result.get());
    }

    @Test
    void checkUpdateShow() {

        MovieDataStorage storage = new MovieDataStorage();
        ShowDAO showDAO = new ShowDataStorageDAO(storage);

        Movie movie = new Movie(1, "Inception", "Sci-Fi", "English", 148);
        storage.movies.put(movie.getMovieId(), movie);

        Screen screen = new Screen(1, "Screen 1", 100);
        storage.screens.put(screen.getScreenId(), screen);

        Show originalShow = new Show(
                1, LocalDate.of(2026, 11, 10),
                LocalTime.of(18, 30), 200.0, screen
        );
        showDAO.add(originalShow, 1, 1);

        Show updatedShow = new Show(
                1, LocalDate.of(2026, 11, 11),
                LocalTime.of(19, 30), 250.0, screen
        );
        updatedShow.setMovieId(1);

        boolean success = showDAO.update(updatedShow);

        assertTrue(success);
        assertEquals(
                LocalDate.of(2026, 11, 11),
                storage.shows.get(1).getShowDate()
        );
        assertEquals(
                LocalTime.of(19, 30),
                storage.shows.get(1).getShowTime()
        );
        assertEquals(250.0, storage.shows.get(1).getBaseTicketPrice());
    }

    @Test
    void checkDeleteShowById() {

        MovieDataStorage storage = new MovieDataStorage();
        ShowDAO showDAO = new ShowDataStorageDAO(storage);

        Movie movie = new Movie(1, "Inception", "Sci-Fi", "English", 148);
        storage.movies.put(movie.getMovieId(), movie);

        Screen screen = new Screen(1, "Screen 1", 100);
        storage.screens.put(screen.getScreenId(), screen);

        Show show = new Show(
                1, LocalDate.of(2026, 11, 10),
                LocalTime.of(18, 30), 200.0, null
        );
        showDAO.add(show, 1, 1);

        boolean success = showDAO.deleteById(1);

        assertTrue(success);
        assertFalse(storage.shows.containsKey(1));
    }

    @Test
    void checkFindShowsByMovieId() {

        MovieDataStorage storage = new MovieDataStorage();
        ShowDAO showDAO = new ShowDataStorageDAO(storage);

        Movie movie1 = new Movie(1, "Inception", "Sci-Fi", "English", 148);
        Movie movie2 = new Movie(2, "Interstellar", "Sci-Fi", "English", 169);
        storage.movies.put(movie1.getMovieId(), movie1);
        storage.movies.put(movie2.getMovieId(), movie2);

        Screen screen = new Screen(1, "Screen 1", 100);
        storage.screens.put(screen.getScreenId(), screen);

        Show show1 = new Show(
                1, LocalDate.of(2026, 11, 10),
                LocalTime.of(18, 30), 200.0, null
        );
        Show show2 = new Show(
                2, LocalDate.of(2026, 11, 11),
                LocalTime.of(15, 0), 180.0, null
        );
        Show show3 = new Show(
                3, LocalDate.of(2026, 11, 12),
                LocalTime.of(20, 0), 220.0, null
        );

        showDAO.add(show1, 1, 1);
        showDAO.add(show2, 1, 1);
        showDAO.add(show3, 2, 1);

        List<Show> shows = showDAO.findByMovieId(1);

        assertEquals(2, shows.size());
        assertTrue(shows.contains(show1));
        assertTrue(shows.contains(show2));
        assertFalse(shows.contains(show3));
    }

    @Test
    void checkFindShowsByScreenId() {

        MovieDataStorage storage = new MovieDataStorage();
        ShowDAO showDAO = new ShowDataStorageDAO(storage);

        Movie movie = new Movie(1, "Inception", "Sci-Fi", "English", 148);
        storage.movies.put(movie.getMovieId(), movie);

        Screen screen1 = new Screen(1, "Screen 1", 100);
        Screen screen2 = new Screen(2, "Screen 2", 150);
        storage.screens.put(screen1.getScreenId(), screen1);
        storage.screens.put(screen2.getScreenId(), screen2);

        Show show1 = new Show(
                1, LocalDate.of(2026, 11, 10),
                LocalTime.of(18, 30), 200.0, null
        );
        Show show2 = new Show(
                2, LocalDate.of(2026, 11, 11),
                LocalTime.of(15, 0), 180.0, null
        );
        Show show3 = new Show(
                3, LocalDate.of(2026, 11, 12),
                LocalTime.of(20, 0), 220.0, null
        );

        showDAO.add(show1, 1, 1);
        showDAO.add(show2, 1, 2);
        showDAO.add(show3, 1, 1);

        List<Show> shows = showDAO.findByScreenId(1);

        assertEquals(2, shows.size());
        assertTrue(shows.contains(show1));
        assertTrue(shows.contains(show3));
        assertFalse(shows.contains(show2));
    }

    @Test
    void checkFindAllShows() {

        MovieDataStorage storage = new MovieDataStorage();
        ShowDAO showDAO = new ShowDataStorageDAO(storage);

        Movie movie = new Movie(1, "Inception", "Sci-Fi", "English", 148);
        storage.movies.put(movie.getMovieId(), movie);

        Screen screen = new Screen(1, "Screen 1", 100);
        storage.screens.put(screen.getScreenId(), screen);

        Show show1 = new Show(
                1, LocalDate.of(2026, 11, 10),
                LocalTime.of(18, 30), 200.0, null
        );
        Show show2 = new Show(
                2, LocalDate.of(2026, 11, 11),
                LocalTime.of(15, 0), 180.0, null
        );

        showDAO.add(show1, 1, 1);
        showDAO.add(show2, 1, 1);

        List<Show> shows = showDAO.findAll();

        assertEquals(2, shows.size());
        assertTrue(shows.contains(show1));
        assertTrue(shows.contains(show2));
    }
}