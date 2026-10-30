package service;

import dao.BookingDAO;
import dao.MovieDAO;
import dao.ScreenDAO;
import dao.ShowDAO;
import model.Booking;
import model.Show;

import java.util.List;
import java.util.Optional;

public class ShowService {

    private final ShowDAO showDAO;
    private final BookingDAO bookingDAO;
    private final MovieDAO movieDAO;
    private final ScreenDAO screenDAO;

    public ShowService(
            ShowDAO showDAO,
            BookingDAO bookingDAO,
            MovieDAO movieDAO,
            ScreenDAO screenDAO) {

        if (showDAO == null || bookingDAO == null
                || movieDAO == null || screenDAO == null) {
            throw new IllegalArgumentException(
                    "DAO dependencies cannot be null."
            );
        }

        this.showDAO = showDAO;
        this.bookingDAO = bookingDAO;
        this.movieDAO = movieDAO;
        this.screenDAO = screenDAO;
    }

    public boolean scheduleShow(Show show, int movieId, int screenId) {
        validateCommonShowFields(show);
        validateId(movieId, "Movie");
        validateId(screenId, "Screen");

        if (show.getShowId() <= 0) {
            throw new IllegalArgumentException(
                    "Show ID must be greater than zero."
            );
        }

        if (movieDAO.findById(movieId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Movie with ID " + movieId + " does not exist."
            );
        }

        if (screenDAO.findById(screenId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Screen with ID " + screenId + " does not exist."
            );
        }

        if (!show.isUpcoming()) {
            throw new IllegalArgumentException(
                    "A show must be scheduled for a future date and time."
            );
        }

        show.setMovieId(movieId);

        return showDAO.add(show, movieId, screenId);
    }

    public boolean updateShow(Show show) {
        validateCommonShowFields(show);

        validateId(show.getShowId(), "Show");

        Optional<Show> existingShow =
                showDAO.findById(show.getShowId());

        if (existingShow.isEmpty()) {
            throw new IllegalArgumentException(
                    "Show with ID " + show.getShowId()
                            + " does not exist."
            );
        }

        if (!existingShow.get().isUpcoming()) {
            throw new IllegalStateException(
                    "A show that has already started or ended "
                            + "cannot be updated."
            );
        }

        if (!show.isUpcoming()) {
            throw new IllegalArgumentException(
                    "The updated show must be scheduled for the future."
            );
        }

        return showDAO.update(show);
    }

    public boolean cancelShow(int showId) {
        validateId(showId, "Show");

        Optional<Show> existingShow = showDAO.findById(showId);

        if (existingShow.isEmpty()) {
            throw new IllegalArgumentException(
                    "Show with ID " + showId + " does not exist."
            );
        }

        List<Booking> bookings = bookingDAO.findByShowId(showId);

        for (Booking booking : bookings) {
            if (booking.isConfirmed()) {
                throw new IllegalStateException(
                        "Cannot cancel this show because it has "
                                + "active bookings."
                );
            }
        }

        return showDAO.deleteById(showId);
    }

    public Optional<Show> findShowById(int showId) {
        validateId(showId, "Show");
        return showDAO.findById(showId);
    }

    public List<Show> findShowsByMovieId(int movieId) {
        validateId(movieId, "Movie");
        return showDAO.findByMovieId(movieId);
    }

    public List<Show> findShowsByScreenId(int screenId) {
        validateId(screenId, "Screen");
        return showDAO.findByScreenId(screenId);
    }

    public List<Show> getAllShows() {
        return showDAO.findAll();
    }

    private void validateCommonShowFields(Show show) {
        if (show == null) {
            throw new IllegalArgumentException(
                    "Show information cannot be null."
            );
        }

        if (show.getShowDate() == null
                || show.getShowTime() == null) {
            throw new IllegalArgumentException(
                    "Show date and time are required."
            );
        }

        double price = show.getBaseTicketPrice();

        if (!Double.isFinite(price) || price <= 0) {
            throw new IllegalArgumentException(
                    "Ticket price must be a positive, finite amount."
            );
        }
    }

    private void validateId(int id, String entityName) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    entityName + " ID must be greater than zero."
            );
        }
    }
}