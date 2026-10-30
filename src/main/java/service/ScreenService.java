package service;

import dao.ScreenDAO;
import dao.SeatDAO;
import dao.ShowDAO;
import dao.TheatreDAO;
import model.Screen;
import model.Seat;
import model.Show;

import java.util.List;
import java.util.Optional;

public class ScreenService {

    private final ScreenDAO screenDAO;
    private final TheatreDAO theatreDAO;
    private final SeatDAO seatDAO;
    private final ShowDAO showDAO;

    public ScreenService(
            ScreenDAO screenDAO,
            TheatreDAO theatreDAO,
            SeatDAO seatDAO,
            ShowDAO showDAO
    ) {
        this.screenDAO = screenDAO;
        this.theatreDAO = theatreDAO;
        this.seatDAO = seatDAO;
        this.showDAO = showDAO;
    }

    public boolean addScreen(Screen screen, int theatreId) {
        validateScreenDetails(screen);

        if (theatreId <= 0) {
            throw new IllegalArgumentException(
                    "Theatre ID must be greater than zero."
            );
        }

        if (theatreDAO.findById(theatreId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Theatre with ID " + theatreId + " does not exist."
            );
        }

        return screenDAO.add(screen, theatreId);
    }

    public boolean updateScreen(Screen screen) {
        validateScreenDetails(screen);

        int screenId = screen.getScreenId();

        Optional<Screen> existingScreen = screenDAO.findById(screenId);

        if (existingScreen.isEmpty()) {
            throw new IllegalArgumentException(
                    "Screen with ID " + screenId + " does not exist."
            );
        }

        if (theatreDAO.findById(screen.getTheatreId()).isEmpty()) {
            throw new IllegalArgumentException(
                    "Theatre with ID " + screen.getTheatreId()
                            + " does not exist."
            );
        }

        return screenDAO.update(screen);
    }

    public boolean removeScreen(int screenId) {
        if (screenId <= 0) {
            throw new IllegalArgumentException(
                    "Screen ID must be greater than zero."
            );
        }

        Optional<Screen> existingScreen = screenDAO.findById(screenId);

        if (existingScreen.isEmpty()) {
            throw new IllegalArgumentException(
                    "Screen with ID " + screenId + " does not exist."
            );
        }

        List<Seat> seats = seatDAO.findByScreenId(screenId);

        if (!seats.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot remove a screen that still has seats."
            );
        }

        List<Show> shows = showDAO.findByScreenId(screenId);

        if (!shows.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot remove a screen that has scheduled shows."
            );
        }

        return screenDAO.deleteById(screenId);
    }

    public Optional<Screen> findScreenById(int screenId) {
        if (screenId <= 0) {
            throw new IllegalArgumentException(
                    "Screen ID must be greater than zero."
            );
        }

        return screenDAO.findById(screenId);
    }

    public List<Screen> findScreensByTheatreId(int theatreId) {
        if (theatreId <= 0) {
            throw new IllegalArgumentException(
                    "Theatre ID must be greater than zero."
            );
        }

        if (theatreDAO.findById(theatreId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Theatre with ID " + theatreId + " does not exist."
            );
        }

        return screenDAO.findByTheatreId(theatreId);
    }

    private void validateScreenDetails(Screen screen) {
        if (screen == null) {
            throw new IllegalArgumentException(
                    "Screen information cannot be null."
            );
        }

        if (screen.getScreenId() <= 0) {
            throw new IllegalArgumentException(
                    "Screen ID must be greater than zero."
            );
        }

        if (screen.getScreenName() == null
                || screen.getScreenName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Screen name cannot be empty."
            );
        }

        if (screen.getCapacity() <= 0) {
            throw new IllegalArgumentException(
                    "Screen capacity must be greater than zero."
            );
        }
    }
}