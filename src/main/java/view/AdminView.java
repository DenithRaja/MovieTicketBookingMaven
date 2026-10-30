package view;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;
import model.Movie;
import model.Screen;
import model.Seat;
import model.Show;
import model.Theatre;


public class AdminView {

    private final Scanner scanner;


    public AdminView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showAdminMenu() {
        System.out.println("================================");
        System.out.println("         ADMIN MENU");
        System.out.println("================================");
        System.out.println("1. Add Movie");
        System.out.println("2. Update Movie");
        System.out.println("3. Remove Movie");
        System.out.println("4. View All Movies");
        System.out.println("5. Search Movie");
        System.out.println("6. Add Theatre");
        System.out.println("7. Add Screen");
        System.out.println("8. Add Seat");
        System.out.println("9. Schedule Show");
        System.out.println("10. Cancel Show");
        System.out.println("11. Logout");
    }

    public int getChoice() {
        return scanner.nextInt();
    }

    public int getMovieId() {
        System.out.print("Enter Movie ID: ");
        return scanner.nextInt();
    }

    public Movie getMovieDetails() {
        int movieId;
        String title;
        String genre;
        String language;
        int duration;

        scanner.nextLine();

        System.out.print("Enter Movie ID: ");
        movieId = scanner.nextInt();

        scanner.nextLine();

        System.out.print("Enter Movie Title: ");
        title = scanner.nextLine();

        System.out.print("Enter Genre: ");
        genre = scanner.nextLine();

        System.out.print("Enter Language: ");
        language = scanner.nextLine();

        System.out.print("Enter Duration: ");
        duration = scanner.nextInt();

        return new Movie(movieId, title, genre, language, duration);
    }

    public Theatre getTheatreDetails() {
        int theatreId;
        String name;
        String location;

        scanner.nextLine();

        System.out.print("Enter Theatre ID: ");
        theatreId = scanner.nextInt();

        scanner.nextLine();

        System.out.print("Enter Theatre Name: ");
        name = scanner.nextLine();

        System.out.print("Enter Location: ");
        location = scanner.nextLine();

        return new Theatre(theatreId, name, location);
    }

    public Screen getScreenDetails() {
        int screenId;
        String screenName;
        int capacity;

        scanner.nextLine();

        System.out.println("Enter Screen ID: ");
        screenId = scanner.nextInt();

        scanner.nextLine();

        System.out.println("Enter Screen Name: ");
        screenName = scanner.nextLine();

        System.out.println("Enter Total Capacity: ");
        capacity = scanner.nextInt();

        return new Screen(screenId, screenName, capacity);
    }

    public Seat getSeatDetails() {
        int seatId;
        String seatNumber;
        String seatRow;
        String seatType;

        scanner.nextLine();

        System.out.print("Enter Seat ID: ");
        seatId = scanner.nextInt();

        scanner.nextLine();

        System.out.print("Enter Seat Number: ");
        seatNumber = scanner.nextLine();

        System.out.print("Enter Seat Row: ");
        seatRow = scanner.nextLine();

        System.out.print("Enter Seat Type: ");
        seatType = scanner.nextLine();

        return new Seat(seatId, seatNumber, seatRow, seatType);
    }

    public Show getShowDetails(Screen screen) {
        int showId;
        LocalDate showDate;
        LocalTime showTime;
        double baseTicketPrice;

        scanner.nextLine();

        System.out.print("Enter Show ID: ");
        showId = scanner.nextInt();

        scanner.nextLine();

        System.out.print("Enter Show Date (yyyy-MM-dd): ");
        showDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter Show Time (HH:mm): ");
        showTime = LocalTime.parse(scanner.nextLine());

        System.out.print("Enter Ticket Price: ");
        baseTicketPrice = scanner.nextDouble();

        return new Show(showId, showDate, showTime, baseTicketPrice, screen);
    }

    public int getShowId() {
        System.out.print("Enter Show ID: ");
        return scanner.nextInt();
    }

    public int getTheatreId() {
        System.out.print("Enter Theatre ID: ");
        return scanner.nextInt();
    }

    public int getScreenId() {
        System.out.print("Enter Screen ID: ");
        return scanner.nextInt();
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}