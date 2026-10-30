package view;

import java.util.Scanner;


public class MainView {

    private final Scanner scanner;


    public MainView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMainMenu() {
        System.out.println("==============================");
        System.out.println("   MOVIE TICKET BOOKING");
        System.out.println("==============================");
        System.out.println("1. Register");
        System.out.println("2. Customer Login");
        System.out.println("3. Admin Login");
        System.out.println("4. Exit");
    }

    public void showPasswordRules() {
        System.out.println("\n========== PASSWORD RULES ==========");
        System.out.println("Your password must:");
        System.out.println("1. Contain at least 8 characters.");
        System.out.println("2. Contain at least one uppercase letter (A-Z).");
        System.out.println("3. Contain at least one lowercase letter (a-z).");
        System.out.println("4. Contain at least one digit (0-9).");
        System.out.println("5. Contain at least one special character (e.g., @, #, !, $).");
        System.out.println("====================================\n");
    }

    public int getChoice() {
        return scanner.nextInt();
    }

    public String getEmail() {
        System.out.print("Enter email: ");
        return scanner.next();
    }

    public String getPassword() {
        System.out.print("Enter password: ");
        return scanner.next();
    }

    public String getName() {
        System.out.print("Enter name: ");
        return scanner.next();
    }

    public String getPhone() {
        System.out.print("Enter phone number: ");
        return scanner.next();
    }

    public int getCustomerId() {
        System.out.print("Enter customer ID: ");
        return scanner.nextInt();
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public String getUsername() {
        System.out.print("Enter username: ");
        return scanner.next();
    }

    public String getAdminUsername() {
        System.out.print("Enter admin username: ");
        return scanner.next();
    }

    public String getAdminPassword() {
        System.out.print("Enter admin password: ");
        return scanner.next();
    }
}