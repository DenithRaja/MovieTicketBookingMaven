
package controller;

import dao.PaymentDAO;
import dao.BookingDAO;
import dao.MovieDAO;
import dao.SeatDAO;
import dao.ShowDAO;
import model.Customer;
import view.CustomerView;
import view.MainView;

public class MainController {
    private final MainView mainView;
    private final CustomerView customerView;
    private final AdminController adminController;
    private final MovieDAO movieDAO;
    private final ShowDAO showDAO;
    private final BookingDAO bookingDAO;
    private final SeatDAO seatDAO;
    private final PaymentDAO paymentDAO;

    public MainController(
            MainView mainView,
            CustomerView customerView,
            AdminController adminController,
            MovieDAO movieDAO,
            ShowDAO showDAO,
            BookingDAO bookingDAO,
            SeatDAO seatDAO, PaymentDAO paymentDAO) {
        this.mainView = mainView;
        this.customerView = customerView;
        this.adminController = adminController;
        this.movieDAO = movieDAO;
        this.showDAO = showDAO;
        this.bookingDAO = bookingDAO;
        this.seatDAO = seatDAO;
        this.paymentDAO = paymentDAO;
    }

    public void start() {
        while (true) {
            mainView.showMainMenu();
            int choice = mainView.getChoice();

            switch (choice) {
                case 1:
                    mainView.showMessage("Select Customer:");
                    mainView.showMessage("1. Dany (ID: 1)");
                    mainView.showMessage("2. Test Customer (ID: 2)");

                    int customerChoice = mainView.getChoice();
                    int customerId;

                    if (customerChoice == 1) {
                        customerId = 1;
                    } else if (customerChoice == 2) {
                        customerId = 2;
                    } else {
                        mainView.showMessage("Invalid customer selection.");
                        break;
                    }

                    CustomerController customerController =
                            new CustomerController(
                                    customerView,
                                    movieDAO,
                                    showDAO,
                                    bookingDAO,
                                    seatDAO,
                                    paymentDAO,
                                    customerId
                            );

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
