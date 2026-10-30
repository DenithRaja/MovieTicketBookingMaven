package controller;

import model.Movie;
import model.Screen;
import model.Seat;
import model.Show;
import model.Theatre;
import service.MovieService;
import service.ScreenService;
import service.SeatService;
import service.ShowService;
import service.TheatreService;
import view.AdminView;

import java.util.List;

public class AdminController {

    private final AdminView adminView;
    private final TheatreService theatreService;
    private final ScreenService screenService;
    private final SeatService seatService;
    private final MovieService movieService;
    private final ShowService showService;

    public AdminController(
            AdminView adminView,
            TheatreService theatreService,
            ScreenService screenService,
            SeatService seatService,
            MovieService movieService,
            ShowService showService
    ) {
        this.adminView = adminView;
        this.theatreService = theatreService;
        this.screenService = screenService;
        this.seatService = seatService;
        this.movieService = movieService;
        this.showService = showService;
    }

    public void start() {
        while (true) {
            adminView.showAdminMenu();
            int choice = adminView.getChoice();

            switch (choice) {
                case 1 -> addMovie();
                case 2 -> updateMovie();
                case 3 -> removeMovie();
                case 4 -> viewAllMovies();
                case 5 -> searchMovie();
                case 6 -> addTheatre();
                case 7 -> addScreen();
                case 8 -> addSeat();
                case 9 -> scheduleShow();
                case 10 -> cancelShow();
                case 11 -> {
                    adminView.showMessage("Logging out...");
                    return;
                }
                default -> adminView.showMessage(
                        "Invalid choice. Please select 1-11."
                );
            }
        }
    }

    private void addMovie() {
        Movie movie = adminView.getMovieDetails();

        try {
            if (movieService.addMovie(movie)) {
                adminView.showMessage("Movie added successfully!");
            } else {
                adminView.showMessage("Failed to add movie.");
            }
        } catch (IllegalArgumentException e) {
            adminView.showMessage(
                    "Failed to add movie: " + e.getMessage()
            );
        }
    }

    private void updateMovie() {
        Movie movie = adminView.getMovieDetails();

        try {
            if (movieService.updateMovie(movie)) {
                adminView.showMessage("Movie updated successfully.");
            } else {
                adminView.showMessage("Failed to update movie.");
            }
        } catch (IllegalArgumentException e) {
            adminView.showMessage(
                    "Failed to update movie: " + e.getMessage()
            );
        }
    }

    private void removeMovie() {
        adminView.showMessage("Enter the Movie ID to remove:");
        int movieId = adminView.getMovieId();

        try {
            if (movieService.removeMovie(movieId)) {
                adminView.showMessage("Movie successfully removed.");
            } else {
                adminView.showMessage("Failed to remove movie.");
            }
        } catch (IllegalArgumentException e) {
            adminView.showMessage(
                    "Failed to remove movie: " + e.getMessage()
            );
        }
    }

    private void viewAllMovies() {
        List<Movie> movies = movieService.getAllMovies();

        if (movies.isEmpty()) {
            adminView.showMessage("No movies found.");
            return;
        }

        for (Movie movie : movies) {
            adminView.showMessage(movie.toString());
        }
    }

    private void searchMovie() {
        adminView.showMessage("Enter title or keyword to search:");
        String keyword = adminView.getMovieDetails().getTitle();

        try {
            List<Movie> results =
                    movieService.searchMoviesByTitle(keyword);

            if (results.isEmpty()) {
                adminView.showMessage("No matching movies found.");
            } else {
                for (Movie movie : results) {
                    adminView.showMessage(movie.toString());
                }
            }
        } catch (IllegalArgumentException e) {
            adminView.showMessage(
                    "Search failed: " + e.getMessage()
            );
        }
    }

    private void addTheatre() {
        Theatre theatre = adminView.getTheatreDetails();

        try {
            if (theatreService.addTheatre(theatre)) {
                adminView.showMessage("Theatre added successfully.");
            } else {
                adminView.showMessage("Failed to add theatre.");
            }
        } catch (IllegalArgumentException e) {
            adminView.showMessage(
                    "Failed to add theatre: " + e.getMessage()
            );
        }
    }

    private void addScreen() {
        Screen screen = adminView.getScreenDetails();
        int theatreId = adminView.getTheatreId();

        try {
            if (screenService.addScreen(screen, theatreId)) {
                adminView.showMessage("Screen added successfully.");
            } else {
                adminView.showMessage("Failed to add screen.");
            }
        } catch (IllegalArgumentException e) {
            adminView.showMessage(
                    "Failed to add screen: " + e.getMessage()
            );
        }
    }

    private void addSeat() {
        Seat seat = adminView.getSeatDetails();
        int screenId = adminView.getScreenId();

        try {
            if (seatService.addSeat(seat, screenId)) {
                adminView.showMessage("Seat added successfully!");
            } else {
                adminView.showMessage("Failed to add seat.");
            }
        } catch (IllegalArgumentException e) {
            adminView.showMessage(
                    "Failed to add seat: " + e.getMessage()
            );
        }
    }

    private void scheduleShow() {
        try {
            int screenId = adminView.getScreenId();

            Screen screen = screenService
                    .findScreenById(screenId)
                    .orElse(null);

            if (screen == null) {
                adminView.showMessage("Screen not found.");
                return;
            }

            Show show = adminView.getShowDetails(screen);
            int movieId = adminView.getMovieId();

            if (showService.scheduleShow(show, movieId, screenId)) {
                adminView.showMessage("Show scheduled successfully!");
            } else {
                adminView.showMessage("Failed to schedule show.");
            }
        } catch (IllegalArgumentException e) {
            adminView.showMessage(
                    "Failed to schedule show: " + e.getMessage()
            );
        }
    }

    private void cancelShow() {
        int showId = adminView.getShowId();

        try {
            if (showService.cancelShow(showId)) {
                adminView.showMessage("Show cancelled successfully!");
            } else {
                adminView.showMessage("Failed to cancel show.");
            }
        } catch (IllegalArgumentException e) {
            adminView.showMessage(
                    "Failed to cancel show: " + e.getMessage()
            );
        }
    }
}