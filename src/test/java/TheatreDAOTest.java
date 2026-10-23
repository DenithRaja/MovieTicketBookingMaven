package dao;

import model.Theatre;
import org.junit.jupiter.api.Test;
import storage.MovieDataStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TheatreDAOTest {

    @Test
    void checkAddTheatre() {
        MovieDataStorage storage = new MovieDataStorage();
        TheatreDAO theatreDAO = new TheatreDataStorageDAO(storage);

        Theatre theatre = new Theatre(1, "PVR", "Anna Nagar");

        boolean success = theatreDAO.add(theatre);

        assertTrue(success);
        assertTrue(storage.theatres.containsKey(1));
        assertEquals(theatre, storage.theatres.get(1));
    }

    @Test
    void checkFindByTheatreID(){
        MovieDataStorage storage = new MovieDataStorage();
        TheatreDAO theatreDAO = new TheatreDataStorageDAO(storage);

        Theatre theatre = new Theatre(1, "PVR", "Anna Nagar");

        theatreDAO.add(theatre);

        assertTrue(theatreDAO.findById(1).isPresent());
        assertEquals(theatre , theatreDAO.findById(1).get());
    }

    @Test
    void checkFindAllTheatres() {
        MovieDataStorage storage = new MovieDataStorage();
        TheatreDAO theatreDAO = new TheatreDataStorageDAO(storage);

        Theatre theatre1 = new Theatre(1, "PVR", "Anna Nagar");
        Theatre theatre2 = new Theatre(2, "INOX", "Chennai");

        theatreDAO.add(theatre1);
        theatreDAO.add(theatre2);

        List<Theatre> theatres = theatreDAO.findAll();

        assertEquals(2,theatres.size());
        assertTrue(theatres.contains(theatre1));
        assertTrue(theatres.contains(theatre2));
    }

    @Test
    void checkUpdateTheatre() {
        MovieDataStorage storage = new MovieDataStorage();
        TheatreDAO theatreDAO = new TheatreDataStorageDAO(storage);

        Theatre originalTheatre = new Theatre(1, "PVR", "Anna Nagar");
        theatreDAO.add(originalTheatre);

        Theatre updatedTheatre = new Theatre(1, "PVR Luxe", "Velachery");

        boolean success  =theatreDAO.update(updatedTheatre);

        Theatre result = theatreDAO.findById(1).get();

        assertTrue(success);
        assertEquals("PVR Luxe", result.getName());
        assertEquals("Velachery", result.getLocation());
    }

    @Test
    void checkDeleteTheatreById() {
        MovieDataStorage storage = new MovieDataStorage();
        TheatreDAO theatreDAO = new TheatreDataStorageDAO(storage);

        Theatre theatre = new Theatre(1, "PVR", "Anna Nagar");
        theatreDAO.add(theatre);

        boolean success = theatreDAO.deleteById(1);

        assertTrue(success);
        assertFalse(storage.theatres.containsKey(1));
    }
}