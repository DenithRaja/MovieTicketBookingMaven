package dao;

import model.Screen;
import storage.MovieDataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScreenDataStorageDAO implements ScreenDAO {
    private final MovieDataStorage storage;

    public ScreenDataStorageDAO(MovieDataStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean add(Screen screen, int theatreId) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen cannot be null");
        }
        if (!storage.theatres.containsKey(theatreId)) {
            throw new IllegalArgumentException("Theatre not found");
        }

        int screenId = screen.getScreenId();
        if (storage.screens.containsKey(screenId)) {
            throw new IllegalArgumentException("Screen ID already exists");
        }

        screen.setTheatreId(theatreId);
        storage.screens.put(screenId, screen);
        return true;
    }

    @Override
    public boolean update(Screen screen) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen cannot be null");
        }

        int screenId = screen.getScreenId();
        if (!storage.screens.containsKey(screenId)) {
            throw new IllegalArgumentException("Screen not found");
        }

        storage.screens.put(screenId, screen);
        return true;
    }

    @Override
    public boolean deleteById(int screenId) {
        if (storage.screens.remove(screenId) == null) {
            throw new IllegalArgumentException("Screen not found");
        }
        return true;
    }

    @Override
    public Optional<Screen> findById(int screenId) {
        Screen screen = storage.screens.get(screenId);
        if (screen == null) {
            return Optional.empty();
        }
        return Optional.of(screen);
    }

    @Override
    public List<Screen> findByTheatreId(int theatreId) {
        List<Screen> screens = new ArrayList<>();
        for (Screen screen : storage.screens.values()) {
            if (screen.getTheatreId() == theatreId) {
                screens.add(screen);
            }
        }
        return screens;
    }
}