package controller;

import dao.ScreenDAO;
import model.Screen;
import dao.TheatreDAO;
import model.Theatre;
import dao.MovieDAO;
import model.Movie;
import view.AdminView;

public class AdminController {
    private AdminView adminView;
    private MovieDAO movieDAO;
    private TheatreDAO theatreDAO;
    private ScreenDAO screenDAO;

    public AdminController(AdminView adminView, MovieDAO movieDAO, TheatreDAO theatreDAO, ScreenDAO screenDAO) {
        this.adminView = adminView;
        this.movieDAO = movieDAO;
        this.theatreDAO = theatreDAO;
        this.screenDAO = screenDAO;
    }

    public void start() {
        while (true) {
            adminView.showAdminMenu();

            int choice = adminView.getChoice();

            switch (choice) {
                case 1:
                    addMovie();
                    break;

                case 2:
                    updateMovie();
                    break;

                case 3:
                    removeMovie();
                    break;

                case 4:
                    addTheatre();
                    break;

                case 5:
                    addScreen();
                    break;

                case 6:
                    adminView.showMessage("Add Seat.");
                    break;

                case 7:
                    adminView.showMessage("Schedule Show.");
                    break;

                case 8:
                    adminView.showMessage("Cancel Show.");
                    break;

                case 9:
                    adminView.showMessage("Logging out...");
                    return;

                default:
                    adminView.showMessage(
                            "Invalid choice. Please select 1-9."
                    );
            }
        }
    }

    private void addMovie(){
        Movie movie = adminView.getMovieDetails();

        if (movieDAO.add(movie)){
            adminView.showMessage("Movie added successfully!..");
        }
        else{
            adminView.showMessage("Failed to add movie.");
        }
    }

    private void updateMovie(){
        Movie movie = adminView.getMovieDetails();

        if(movieDAO.update(movie)){
            adminView.showMessage("Movie updated successfully");
        }
        else{
            adminView.showMessage("Failed to update movie..Movie ID may not exist");
        }
    }

    private void removeMovie(){
        adminView.showMessage("Enter the Movie ID to remove : ");
        int movieId = adminView.getMovieId();

        if (movieDAO.deleteById(movieId)){
            adminView.showMessage("Movie successfully removed..");
        }
        else{
            adminView.showMessage("Failed to remove movie. Movie ID may not exist.");
        }
    }

    private void addTheatre(){
        Theatre theatre = adminView.getTheatreDetails();

        if (theatreDAO.add(theatre)){
            adminView.showMessage("Theatre added successfully");
        }
        else{
            adminView.showMessage("Failed to add theatre. Theatre ID may not exist");
        }
    }

    private void addScreen(){
        Screen screen = adminView.getScreenDetails();
        int theatreId = adminView.getTheatreId();

        try{
            if (screenDAO.add(screen, theatreId)){
                adminView.showMessage("Screen added successfully");
            }
            else{
                adminView.showMessage("Failed to add screen");
            }
        }catch (IllegalArgumentException e){
            adminView.showMessage("Failed to add screen " + e.getMessage());
        }
    }

}
