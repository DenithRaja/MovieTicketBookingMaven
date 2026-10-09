package app;

import controller.AdminController;
import controller.CustomerController;
import controller.MainController;

import view.AdminView;
import view.CustomerView;
import view.MainView;

import java.util.Scanner;

public class MovieTicketBookingApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        MainView mainView = new MainView(scanner);
        CustomerView customerView = new CustomerView(scanner);
        AdminView adminView = new AdminView(scanner);

        CustomerController customerController =
                new CustomerController(customerView);

        AdminController adminController =
                new AdminController(adminView);

        MainController mainController =
                new MainController(
                        mainView,
                        customerController,
                        adminController
                );

        mainController.start();
    }
}