package service;

import dao.ScreenDAO;
import dao.TheatreDAO;
import model.Screen;
import model.Theatre;

import java.util.List;
import java.util.Optional;

public class TheatreService {

    private final TheatreDAO theatreDAO;
    private final ScreenDAO screenDAO;

    public TheatreService(TheatreDAO theatreDAO, ScreenDAO screenDAO) {
        this.theatreDAO = theatreDAO;
        this.screenDAO = screenDAO;
    }

    public boolean addTheatre(Theatre theatre) {
        validateTheatreDetails(theatre);
        return theatreDAO.add(theatre);
    }

    public boolean updateTheatre(Theatre theatre) {
        validateTheatreDetails(theatre);

        int theatreId = theatre.getTheatreId();

        Optional<Theatre> existingTheatre =
                theatreDAO.findById(theatreId);

        if (existingTheatre.isEmpty()) {
            throw new IllegalArgumentException(
                    "Theatre with ID " + theatreId + " does not exist."
            );
        }

        return theatreDAO.update(theatre);
    }

    public boolean removeTheatre(int theatreId) {
        if (theatreId <= 0) {
            throw new IllegalArgumentException(
                    "Theatre ID must be greater than zero."
            );
        }

        Optional<Theatre> existingTheatre =
                theatreDAO.findById(theatreId);

        if (existingTheatre.isEmpty()) {
            throw new IllegalArgumentException(
                    "Theatre with ID " + theatreId + " does not exist."
            );
        }

        List<Screen> screens = screenDAO.findByTheatreId(theatreId);

        if (!screens.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot remove a theatre that still has screens."
            );
        }

        return theatreDAO.deleteById(theatreId);
    }

    public Optional<Theatre> findTheatreById(int theatreId) {
        if (theatreId <= 0) {
            throw new IllegalArgumentException(
                    "Theatre ID must be greater than zero."
            );
        }

        return theatreDAO.findById(theatreId);
    }

    public List<Theatre> getAllTheatres() {
        return theatreDAO.findAll();
    }

    private void validateTheatreDetails(Theatre theatre) {
        if (theatre == null) {
            throw new IllegalArgumentException(
                    "Theatre information cannot be null."
            );
        }

        if (theatre.getTheatreId() <= 0) {
            throw new IllegalArgumentException(
                    "Theatre ID must be greater than zero."
            );
        }

        if (theatre.getName() == null || theatre.getName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Theatre name cannot be empty."
            );
        }

        if (theatre.getLocation() == null || theatre.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Theatre location cannot be empty."
            );
        }
    }
}