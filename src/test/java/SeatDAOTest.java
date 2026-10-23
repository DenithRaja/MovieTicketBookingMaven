package dao;

import model.Screen;
import model.Seat;
import org.junit.jupiter.api.Test;
import storage.MovieDataStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SeatDAOTest {

    @Test
    void checkAddSeat() {

        MovieDataStorage storage = new MovieDataStorage();
        SeatDAO seatDAO = new SeatDataStorageDAO(storage);

        Screen screen = new Screen(1, "Screen 1", 100);
        storage.screens.put(screen.getScreenId(), screen);

        Seat seat = new Seat(1, "A1", "A", "PREMIUM");

        boolean success = seatDAO.add(seat, 1);

        assertTrue(success);
        assertTrue(storage.seats.containsKey(1));
        assertEquals(seat, storage.seats.get(1));
        assertEquals(1, storage.seatScreenIds.get(1));
    }

    @Test
    void checkFindSeatById() {

        MovieDataStorage storage = new MovieDataStorage();
        SeatDAO seatDAO = new SeatDataStorageDAO(storage);

        Screen screen = new Screen(1, "Screen 1", 100);
        storage.screens.put(screen.getScreenId(), screen);

        Seat seat = new Seat(1, "A1", "A", "PREMIUM");
        seatDAO.add(seat, 1);

        Optional<Seat> result = seatDAO.findById(1);

        assertTrue(result.isPresent());
        assertEquals(seat, result.get());
    }

    @Test
    void checkUpdateSeat() {

        MovieDataStorage storage = new MovieDataStorage();
        SeatDAO seatDAO = new SeatDataStorageDAO(storage);

        Screen screen = new Screen(1, "Screen 1", 100);
        storage.screens.put(screen.getScreenId(), screen);

        Seat originalSeat = new Seat(1, "A1", "A", "PREMIUM");
        seatDAO.add(originalSeat, 1);

        Seat updatedSeat = new Seat(1, "A2", "A", "REGULAR");

        boolean success = seatDAO.update(updatedSeat);

        assertTrue(success);
        assertEquals(updatedSeat, storage.seats.get(1));
        assertEquals("A2", storage.seats.get(1).getSeatNumber());
        assertEquals("REGULAR", storage.seats.get(1).getSeatType());

        // The screen relationship should remain unchanged.
        assertEquals(1, storage.seatScreenIds.get(1));
    }

    @Test
    void checkDeleteSeatById() {

        MovieDataStorage storage = new MovieDataStorage();
        SeatDAO seatDAO = new SeatDataStorageDAO(storage);

        Screen screen = new Screen(1, "Screen 1", 100);
        storage.screens.put(screen.getScreenId(), screen);

        Seat seat = new Seat(1, "A1", "A", "PREMIUM");
        seatDAO.add(seat, 1);

        boolean success = seatDAO.deleteById(1);

        assertTrue(success);
        assertFalse(storage.seats.containsKey(1));
        assertFalse(storage.seatScreenIds.containsKey(1));
    }

    @Test
    void checkFindSeatsByScreenId() {

        MovieDataStorage storage = new MovieDataStorage();
        SeatDAO seatDAO = new SeatDataStorageDAO(storage);

        Screen screen1 = new Screen(1, "Screen 1", 100);
        Screen screen2 = new Screen(2, "Screen 2", 150);

        storage.screens.put(screen1.getScreenId(), screen1);
        storage.screens.put(screen2.getScreenId(), screen2);

        Seat seat1 = new Seat(1, "A1", "A", "PREMIUM");
        Seat seat2 = new Seat(2, "A2", "A", "REGULAR");
        Seat seat3 = new Seat(3, "B1", "B", "PREMIUM");

        seatDAO.add(seat1, 1);
        seatDAO.add(seat2, 1);
        seatDAO.add(seat3, 2);

        List<Seat> seats = seatDAO.findByScreenId(1);

        assertEquals(2, seats.size());
        assertTrue(seats.contains(seat1));
        assertTrue(seats.contains(seat2));
        assertFalse(seats.contains(seat3));
    }
}