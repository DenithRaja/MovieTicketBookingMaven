package controller;

import view.CustomerView;

public class CustomerController {
    private CustomerView customerView;

    public CustomerController(CustomerView customerView) {
        this.customerView = customerView;
    }

    public void start() {
        while (true) {
            customerView.showCustomerMenu();

            int choice = customerView.getChoice();

            switch (choice) {
                case 1:
                    customerView.showMessage(
                            "View Movies."
                    );
                    break;

                case 2:
                    customerView.showMessage(
                            "Search Movies."
                    );
                    break;

                case 3:
                    customerView.showMessage(
                            "View Shows."
                    );
                    break;

                case 4:
                    customerView.showMessage(
                            "Booking."
                    );
                    break;

                case 5:
                    customerView.showMessage(
                            "Booking history."
                    );
                    break;

                case 6:
                    customerView.showMessage(
                            "Cancellation."
                    );
                    break;

                case 7:
                    customerView.showMessage("Logging out...");
                    return;

                default:
                    customerView.showMessage(
                            "Invalid choice. Please select 1-7."
                    );
            }
        }
    }
}
