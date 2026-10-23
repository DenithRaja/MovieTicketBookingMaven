package dao;

import model.Screen;
import model.Theatre;
import org.junit.jupiter.api.Test;
import storage.MovieDataStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ScreenDAOTest {

    @Test
    void checkAddScreen() {

        // Arrange
        MovieDataStorage storage = new MovieDataStorage();
        ScreenDAO screenDAO = new ScreenDataStorageDAO(storage);

        Theatre theatre = new Theatre(1, "PVR", "Anna Nagar");
        storage.theatres.put(theatre.getTheatreId(), theatre);

        Screen screen = new Screen(1, "Screen 1", 100);

        boolean success = screenDAO.add(screen, 1);

        assertTrue(success);
        assertTrue(storage.screens.containsKey(1));
        assertEquals(screen, storage.screens.get(1));
        assertEquals(1, screen.getTheatreId());

    }

    @Test
    void checkFindByScreenID() {
        // Arrange
        MovieDataStorage storage = new MovieDataStorage();
        ScreenDAO screenDAO = new ScreenDataStorageDAO(storage);

        Theatre theatre = new Theatre(1, "PVR", "Anna Nagar");
        storage.theatres.put(theatre.getTheatreId(), theatre);

        Screen screen = new Screen(1, "Screen 1", 100);
        screenDAO.add(screen, 1);

        Optional<Screen> result = screenDAO.findById(1);

        assertTrue(result.isPresent());
        assertEquals(screen, result.get());
    }

    @Test
    void checkUpdateScreen() {
        // Arrange
        MovieDataStorage storage = new MovieDataStorage();
        ScreenDAO screenDAO = new ScreenDataStorageDAO(storage);

        Theatre theatre = new Theatre(1, "PVR", "Anna Nagar");
        storage.theatres.put(theatre.getTheatreId(), theatre);

        Screen originalScreen = new Screen(1, "Screen 1", 100);
        screenDAO.add(originalScreen, 1);

        Screen updatedScreen = new Screen(1, "Screen 1 Updated", 150);
        updatedScreen.setTheatreId(1);

        boolean success = screenDAO.update(updatedScreen);

        assertTrue(success);
        assertEquals("Screen 1 Updated", storage.screens.get(1).getScreenName());
        assertEquals(150, storage.screens.get(1).getCapacity());
    }

    @Test
    void checkDeleteScreenById() {
        // Arrange
        MovieDataStorage storage = new MovieDataStorage();
        ScreenDAO screenDAO = new ScreenDataStorageDAO(storage);

        Theatre theatre = new Theatre(1, "PVR", "Anna Nagar");
        storage.theatres.put(theatre.getTheatreId(), theatre);

        Screen screen = new Screen(1, "Screen 1", 100);
        screenDAO.add(screen, 1);

        boolean success = screenDAO.deleteById(1);

        assertTrue(success);
        assertFalse(storage.screens.containsKey(1));
    }

    @Test
    void checkFindScreensByTheatreID() {
        // Arrange
        MovieDataStorage storage = new MovieDataStorage();
        ScreenDAO screenDAO = new ScreenDataStorageDAO(storage);

        Theatre theatre1 = new Theatre(1, "PVR", "Anna Nagar");
        Theatre theatre2 = new Theatre(2, "INOX", "Chennai");

        storage.theatres.put(theatre1.getTheatreId(), theatre1);
        storage.theatres.put(theatre2.getTheatreId(), theatre2);

        Screen screen1 = new Screen(1, "Screen 1", 100);
        Screen screen2 = new Screen(2, "Screen 2", 150);
        Screen screen3 = new Screen(3, "Screen 3", 200);

        screenDAO.add(screen1, 1);
        screenDAO.add(screen2, 1);
        screenDAO.add(screen3, 2);

        List<Screen> screens = screenDAO.findByTheatreId(1);

        assertEquals(2, screens.size());
        assertTrue(screens.contains(screen1));
        assertTrue(screens.contains(screen2));
        assertFalse(screens.contains(screen3));
    }
}
