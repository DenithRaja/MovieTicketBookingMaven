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

    public void askShowId() {
        System.out.print("Enter Show ID: ");
    }

    public void askSeatNumbers() {
        System.out.print("Enter seat numbers separated by commas (e.g. A1,A2): ");
    }

    public String getPaymentMethod() {
        System.out.println("\nSelect Payment Method:");
        System.out.println("1. UPI");
        System.out.println("2. Card");
        System.out.println("3. Cash");
        System.out.print("Enter your choice: ");

        String input = scanner.nextLine().trim();

        switch (input) {
            case "1":
                return "UPI";
            case "2":
                return "CARD";
            case "3":
                return "CASH";
            default:
                showMessage("Invalid Payment Choice...");
                return null;
        }
    }
}
