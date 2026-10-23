package dao;

import model.Seat;
import storage.MovieDataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeatDataStorageDAO implements SeatDAO {
    private final MovieDataStorage storage;

    public SeatDataStorageDAO(MovieDataStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean add(Seat seat, int screenId) {
        if (seat == null) {
            throw new IllegalArgumentException("Seat cannot be null");
        }
        if (!storage.screens.containsKey(screenId)) {
            throw new IllegalArgumentException("Screen not found");
        }

        int seatId = seat.getSeatId();
        if (storage.seats.containsKey(seatId)) {
            throw new IllegalArgumentException("Seat ID already exists");
        }

        storage.seats.put(seatId, seat);
        storage.seatScreenIds.put(seatId, screenId);
        return true;
    }

    @Override
    public boolean update(Seat seat) {
        if (seat == null) {
            throw new IllegalArgumentException("Seat cannot be null");
        }

        int seatId = seat.getSeatId();
        if (!storage.seats.containsKey(seatId)) {
            throw new IllegalArgumentException("Seat not found");
        }

        storage.seats.put(seatId, seat);
        return true;
    }

    @Override
    public boolean deleteById(int seatId) {
        if (!storage.seats.containsKey(seatId)) {
            throw new IllegalArgumentException("Seat not found");
        }

        storage.seats.remove(seatId);
        storage.seatScreenIds.remove(seatId);
        return true;
    }

    @Override
    public Optional<Seat> findById(int seatId) {
        Seat seat = storage.seats.get(seatId);
        if (seat == null) {
            return Optional.empty();
        }
        return Optional.of(seat);
    }

    @Override
    public List<Seat> findByScreenId(int screenId) {
        List<Seat> seats = new ArrayList<>();
        for (Integer seatId : storage.seatScreenIds.keySet()) {
            if (storage.seatScreenIds.get(seatId) == screenId) {
                Seat seat = storage.seats.get(seatId);
                if (seat != null) {
                    seats.add(seat);
                }
            }
        }
        return seats;
    }
}