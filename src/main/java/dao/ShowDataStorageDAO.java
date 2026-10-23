package dao;

import model.Show;
import storage.MovieDataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShowDataStorageDAO implements ShowDAO {
    private final MovieDataStorage storage;

    public ShowDataStorageDAO(MovieDataStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean add(Show show, int movieId, int screenId) {
        if (show == null) {
            throw new IllegalArgumentException("Show cannot be null");
        }
        if (!storage.movies.containsKey(movieId)) {
            throw new IllegalArgumentException("Movie not found");
        }
        if (!storage.screens.containsKey(screenId)) {
            throw new IllegalArgumentException("Screen not found");
        }

        int showId = show.getShowId();
        if (storage.shows.containsKey(showId)) {
            throw new IllegalArgumentException("Show ID already exists");
        }

        show.setMovieId(movieId);
        show.setScreen(storage.screens.get(screenId));
        storage.shows.put(showId, show);
        return true;
    }

    @Override
    public boolean update(Show show) {
        if (show == null) {
            throw new IllegalArgumentException("Show cannot be null");
        }

        int showId = show.getShowId();
        if (!storage.shows.containsKey(showId)) {
            throw new IllegalArgumentException("Show not found");
        }
        if (!storage.movies.containsKey(show.getMovieId())) {
            throw new IllegalArgumentException("Movie not found");
        }
        if (show.getScreen() == null || !storage.screens.containsKey(show.getScreen().getScreenId())) {
            throw new IllegalArgumentException("Screen not found");
        }

        storage.shows.put(showId, show);
        return true;
    }

    @Override
    public boolean deleteById(int showId) {
        if (storage.shows.remove(showId) == null) {
            throw new IllegalArgumentException("Show not found");
        }
        return true;
    }

    @Override
    public Optional<Show> findById(int showId) {
        Show show = storage.shows.get(showId);
        if (show == null) {
            return Optional.empty();
        }
        return Optional.of(show);
    }

    @Override
    public List<Show> findByMovieId(int movieId) {
        List<Show> shows = new ArrayList<>();
        for (Show show : storage.shows.values()) {
            if (show.getMovieId() == movieId) {
                shows.add(show);
            }
        }
        return shows;
    }

    @Override
    public List<Show> findByScreenId(int screenId) {
        List<Show> shows = new ArrayList<>();
        for (Show show : storage.shows.values()) {
            if (show.getScreen() != null && show.getScreen().getScreenId() == screenId) {
                shows.add(show);
            }
        }
        return shows;
    }

    @Override
    public List<Show> findAll() {
        return new ArrayList<>(storage.shows.values());
    }
}