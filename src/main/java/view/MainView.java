package view;

import java.util.Scanner;

public class MainView {
    private Scanner scanner;

    public MainView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMainMenu() {
        System.out.println("==============================");
        System.out.println("   MOVIE TICKET BOOKING");
        System.out.println("==============================");
        System.out.println("1. Customer");
        System.out.println("2. Admin");
        System.out.println("3. Exit");
    }

    public int getChoice() {
        return scanner.nextInt();
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}
