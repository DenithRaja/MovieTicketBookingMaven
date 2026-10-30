
package impl;

import dao.MovieDAO;
import dao.ScreenDAO;
import dao.ShowDAO;
import model.Show;
import model.Screen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ShowDataStorageDAO implements ShowDAO {

    private final Map<Integer, Show> shows = new LinkedHashMap<>();

    private final MovieDAO movieDAO;
    private final ScreenDAO screenDAO;

    public ShowDataStorageDAO(MovieDAO movieDAO, ScreenDAO screenDAO) {
        this.movieDAO = movieDAO;
        this.screenDAO = screenDAO;
    }

    @Override
    public boolean add(Show show, int movieId, int screenId) {
        if (show == null) {
            throw new IllegalArgumentException("Show cannot be null");
        }

        if (movieDAO.findById(movieId).isEmpty()) {
            throw new IllegalArgumentException("Movie not found");
        }

        Optional<Screen> screenOptional = screenDAO.findById(screenId);

        if (screenOptional.isEmpty()) {
            throw new IllegalArgumentException("Screen not found");
        }

        int showId = show.getShowId();

        if (shows.containsKey(showId)) {
            throw new IllegalArgumentException("Show ID already exists");
        }

        show.setMovieId(movieId);
        show.setScreen(screenOptional.get());

        shows.put(showId, show);

        return true;
    }

    @Override
    public boolean update(Show show) {
        if (show == null) {
            throw new IllegalArgumentException("Show cannot be null");
        }

        int showId = show.getShowId();

        if (!shows.containsKey(showId)) {
            throw new IllegalArgumentException("Show not found");
        }

        if (movieDAO.findById(show.getMovieId()).isEmpty()) {
            throw new IllegalArgumentException("Movie not found");
        }

        if (show.getScreen() == null
                || screenDAO.findById(show.getScreen().getScreenId()).isEmpty()) {
            throw new IllegalArgumentException("Screen not found");
        }

        shows.put(showId, show);

        return true;
    }

    @Override
    public boolean deleteById(int showId) {
        if (shows.remove(showId) == null) {
            throw new IllegalArgumentException("Show not found");
        }

        return true;
    }

    @Override
    public Optional<Show> findById(int showId) {
        return Optional.ofNullable(shows.get(showId));
    }

    @Override
    public List<Show> findByMovieId(int movieId) {
        List<Show> matchingShows = new ArrayList<>();

        for (Show show : shows.values()) {
            if (show.getMovieId() == movieId) {
                matchingShows.add(show);
            }
        }

        return matchingShows;
    }

    @Override
    public List<Show> findByScreenId(int screenId) {
        List<Show> matchingShows = new ArrayList<>();

        for (Show show : shows.values()) {
            if (show.getScreen() != null
                    && show.getScreen().getScreenId() == screenId) {
                matchingShows.add(show);
            }
        }

        return matchingShows;
    }

    @Override
    public List<Show> findAll() {
        return new ArrayList<>(shows.values());
    }
}
