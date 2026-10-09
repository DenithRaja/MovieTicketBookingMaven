package view;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CustomerView {
    private Scanner scanner;

    public CustomerView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showCustomerMenu() {
        System.out.println("================================");
        System.out.println("       CUSTOMER MENU");
        System.out.println("================================");
        System.out.println("1. View Movies");
        System.out.println("2. Search Movies");
        System.out.println("3. View Shows");
        System.out.println("4. Book Ticket");
        System.out.println("5. View My Bookings");
        System.out.println("6. Cancel Booking");
        System.out.println("7. Logout");
    }

    public int getChoice(){
        return scanner.nextInt();
    }

    public String getMovieSearchText(){
        scanner.nextLine();
        return scanner.nextLine();
    }

    public int getMovieId(){
        return scanner.nextInt();
    }

    public int getShowId(){
        return scanner.nextInt();
    }

    public List<String> getSeatNumbers(){
        scanner.nextLine();
        String input = scanner.nextLine();
        return Arrays.asList(input.split(","));
    }

    public int getBookingId(){
        return scanner.nextInt();
    }

    public void showMessage(String message){
        System.out.println(message);
    }
}
