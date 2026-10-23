package view;

import model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class AdminView {
    private Scanner scanner;

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
        System.out.println("4. Add Theatre");
        System.out.println("5. Add Screen");
        System.out.println("6. Add Seat");
        System.out.println("7. Schedule Show");
        System.out.println("8. Cancel Show");
        System.out.println("9. Logout");
    }

    public int getChoice(){
        return scanner.nextInt();
    }

    public int getMovieId(){
        return scanner.nextInt();
    }

    public Movie getMovieDetails() {
        scanner.nextLine();

        System.out.print("Enter Movie ID: ");
        int movieId = scanner.nextInt();

        scanner.nextLine();

        System.out.print("Enter Movie Title: ");
        String title = scanner.nextLine();

        System.out.print("Enter Genre: ");
        String genre = scanner.nextLine();

        System.out.print("Enter Language: ");
        String language = scanner.nextLine();

        System.out.print("Enter Duration: ");
        int duration = scanner.nextInt();

        return new Movie(movieId, title, genre, language, duration);
    }

    public Theatre getTheatreDetails() {
        scanner.nextLine();

        System.out.print("Enter Theatre ID: ");
        int theatreId = scanner.nextInt();

        scanner.nextLine();

        System.out.print("Enter Theatre Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Location: ");
        String location = scanner.nextLine();

        return new Theatre(theatreId, name, location);
    }

    public Screen getScreenDetails(){
        scanner.nextLine();

        System.out.println("Enter Screen ID: ");
        int screenId = scanner.nextInt();

        scanner.nextLine();

        System.out.println("Enter Screen Name: ");
        String screenName = scanner.nextLine();

        System.out.println("Enter Total Capacity: ");
        int capacity = scanner.nextInt();

        return new Screen(screenId,screenName,capacity);
    }

    public Seat getSeatDetails() {
        scanner.nextLine();

        System.out.print("Enter Seat ID: ");
        int seatId = scanner.nextInt();

        scanner.nextLine();

        System.out.print("Enter Seat Number: ");
        String seatNumber = scanner.nextLine();

        System.out.print("Enter Seat Row: ");
        String seatRow = scanner.nextLine();

        System.out.print("Enter Seat Type: ");
        String seatType = scanner.nextLine();

        return new Seat(seatId, seatNumber, seatRow, seatType);
    }

    public Show getShowDetails(Screen screen) {
        scanner.nextLine();

        System.out.print("Enter Show ID: ");
        int showId = scanner.nextInt();

        scanner.nextLine();

        System.out.print("Enter Show Date (yyyy-MM-dd): ");
        LocalDate showDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter Show Time (HH:mm): ");
        LocalTime showTime = LocalTime.parse(scanner.nextLine());

        System.out.print("Enter Ticket Price: ");
        double baseTicketPrice = scanner.nextDouble();

        return new Show(showId, showDate, showTime, baseTicketPrice, screen);
    }

    public int getShowId(){
        return scanner.nextInt();
    }

    public int getTheatreId() {
        System.out.print("Enter Theatre ID: ");
        return scanner.nextInt();
    }

    public void showMessage(String message){
        System.out.println(message);
    }
}
