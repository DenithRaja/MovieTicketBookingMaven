import model.Movie;
import model.Screen;
import model.Seat;
import model.Show;
import model.Theatre;
import view.AdminView;

import java.util.Scanner;

public class ViewTest {

    public static void main(String[] args) {

        AdminView view = new AdminView(new Scanner("fff"));

        // 1. Show admin menu
        view.showAdminMenu();

        // 2. Get menu choice
        System.out.print("Enter choice: ");
        int choice = view.getChoice();
        System.out.println("You selected: " + choice);

        // 3. Get Movie ID
        System.out.print("Enter Movie ID: ");
        int movieId = view.getMovieId();
        System.out.println("Movie ID: " + movieId);

        // 4. Get Movie details
        Movie movie = view.getMovieDetails();
        System.out.println("Created Movie: " + movie);

        // 5. Get Theatre details
        Theatre theatre = view.getTheatreDetails();
        System.out.println("Created Theatre: " + theatre);

        // 6. Get Screen details
        Screen screen = view.getScreenDetails();
        System.out.println("Created Screen: " + screen);

        // 7. Get Seat details
        Seat seat = view.getSeatDetails();
        System.out.println("Created Seat: " + seat);

        // 8. Get Show details
        Show show = view.getShowDetails(screen);
        System.out.println("Created Show: " + show);

        // 9. Get Show ID
        System.out.print("Enter Show ID: ");
        int showId = view.getShowId();
        System.out.println("Show ID: " + showId);

        // 10. Show message
        view.showMessage("Admin View test completed!");
    }
}