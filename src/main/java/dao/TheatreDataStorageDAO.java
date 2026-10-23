package dao;

import model.Theatre;
import storage.MovieDataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TheatreDataStorageDAO implements TheatreDAO {
    private final MovieDataStorage storage;

    public TheatreDataStorageDAO(MovieDataStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean add(Theatre theatre) {
        if (theatre == null) {
            throw new IllegalArgumentException("Theatre cannot be null");
        }

        int theatreId = theatre.getTheatreId();
        if (storage.theatres.containsKey(theatreId)) {
            throw new IllegalArgumentException("Theatre ID already exists");
        }

        storage.theatres.put(theatreId, theatre);
        return true;
    }

    @Override
    public boolean update(Theatre theatre) {
        if (theatre == null) {
            throw new IllegalArgumentException("Theatre cannot be null");
        }

        int theatreId = theatre.getTheatreId();
        if (!storage.theatres.containsKey(theatreId)) {
            throw new IllegalArgumentException("Theatre not found");
        }

        storage.theatres.put(theatreId, theatre);
        return true;
    }

    @Override
    public boolean deleteById(int theatreId) {
        if (storage.theatres.remove(theatreId) == null) {
            throw new IllegalArgumentException("Theatre not found");
        }
        return true;
    }

    @Override
    public Optional<Theatre> findById(int theatreId) {
        Theatre theatre = storage.theatres.get(theatreId);
        if (theatre == null) {
            return Optional.empty();
        }
        return Optional.of(theatre);
    }

    @Override
    public List<Theatre> findAll() {
        return new ArrayList<>(storage.theatres.values());
    }
}