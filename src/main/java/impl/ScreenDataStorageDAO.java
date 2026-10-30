
package impl;

import dao.ScreenDAO;
import dao.TheatreDAO;
import model.Screen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ScreenDataStorageDAO implements ScreenDAO {

    private final Map<Integer, Screen> screens = new LinkedHashMap<>();
    private final TheatreDAO theatreDAO;

    public ScreenDataStorageDAO(TheatreDAO theatreDAO) {
        this.theatreDAO = theatreDAO;
    }

    @Override
    public boolean add(Screen screen, int theatreId) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen cannot be null");
        }

        if (theatreDAO.findById(theatreId).isEmpty()) {
            throw new IllegalArgumentException("Theatre not found");
        }

        int screenId = screen.getScreenId();

        if (screens.containsKey(screenId)) {
            throw new IllegalArgumentException("Screen ID already exists");
        }

        screen.setTheatreId(theatreId);
        screens.put(screenId, screen);

        return true;
    }

    @Override
    public boolean update(Screen screen) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen cannot be null");
        }

        int screenId = screen.getScreenId();

        if (!screens.containsKey(screenId)) {
            throw new IllegalArgumentException("Screen not found");
        }

        screens.put(screenId, screen);
        return true;
    }

    @Override
    public boolean deleteById(int screenId) {
        if (screens.remove(screenId) == null) {
            throw new IllegalArgumentException("Screen not found");
        }

        return true;
    }

    @Override
    public Optional<Screen> findById(int screenId) {
        return Optional.ofNullable(screens.get(screenId));
    }

    @Override
    public List<Screen> findByTheatreId(int theatreId) {
        List<Screen> matchingScreens = new ArrayList<>();

        for (Screen screen : screens.values()) {
            if (screen.getTheatreId() == theatreId) {
                matchingScreens.add(screen);
            }
        }

        return matchingScreens;
    }
}
