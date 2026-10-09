package controller;

import view.MainView;

public class MainController {
    private MainView mainView;
    private CustomerController customerController;
    private AdminController adminController;

    public MainController(MainView mainView, CustomerController customerController, AdminController adminController) {
        this.mainView = mainView;
        this.customerController = customerController;
        this.adminController = adminController;
    }

    public void start() {

        while (true) {

            mainView.showMainMenu();

            int choice = mainView.getChoice();

            switch (choice) {

                case 1:
                    customerController.start();
                    break;

                case 2:
                    adminController.start();
                    break;

                case 3:
                    mainView.showMessage(
                            "Thank you for using Movie Ticket Booking!"
                    );
                    return;

                default:
                    mainView.showMessage(
                            "Invalid choice. Please select 1, 2, or 3."
                    );
            }
        }
    }
}
