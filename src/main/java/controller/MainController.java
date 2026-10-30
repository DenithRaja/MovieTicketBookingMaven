package controller;

import dao.CustomerDAO;
import model.Customer;
import service.AuthenticationService;
import view.CustomerView;
import view.MainView;

public class MainController {

    private final MainView mainView;
    private final CustomerView customerView;
    private final AdminController adminController;
    private final CustomerController customerController;
    private final AuthenticationController authenticationController;
    private final CustomerDAO customerDAO;

    public MainController(
            MainView mainView,
            CustomerView customerView,
            AdminController adminController,
            CustomerController customerController,
            AuthenticationController authenticationController,
            CustomerDAO customerDAO
    ) {
        this.mainView = mainView;
        this.customerView = customerView;
        this.adminController = adminController;
        this.customerController = customerController;
        this.authenticationController = authenticationController;
        this.customerDAO = customerDAO;
    }

    public void start() {
        while (true) {
            mainView.showMainMenu();
            int choice = mainView.getChoice();

            switch (choice) {
                case 1 -> registerCustomer();
                case 2 -> loginCustomer();
                case 3 -> loginAdmin();
                case 4 -> {
                    mainView.showMessage(
                            "Thank you for using Movie Ticket Booking!"
                    );
                    return;
                }
                default -> mainView.showMessage("Invalid choice. Please select 1, 2, 3, or 4."
                );
            }
        }
    }

    private void loginAdmin() {
        String username = mainView.getAdminUsername();
        String password = mainView.getAdminPassword();

        if (service.AdminAuthenticationService.login(username, password)) {
            mainView.showMessage("Admin login successful!");

            try {
                adminController.start();
            } finally {
                service.AdminAuthenticationService.logout();
                mainView.showMessage("Admin logged out.");
            }
        } else {
            mainView.showMessage(
                    "Invalid admin username or password."
            );
        }
    }

    private void registerCustomer() {
        int customerId = customerDAO.findAll().stream()
                .mapToInt(Customer::getUserId)
                .max()
                .orElse(0) + 1;

        AuthenticationService authService =
                authenticationController.getAuthenticationService();

        String name = askForName(authService);
        String username = askForUsername(authService);
        String email = askForEmail(authService);
        String phone = askForPhone(authService);
        String password = askForPassword(authService);

        authenticationController.register(
                customerId,
                name,
                username,
                email,
                phone,
                password
        );
    }

    private String askForName(AuthenticationService authService) {
        while (true) {
            String name = mainView.getName();

            try {
                authService.validateName(name);
                return name;
            } catch (IllegalArgumentException exception) {
                mainView.showMessage(exception.getMessage());
                mainView.showMessage("Please enter your name again.");
            }
        }
    }

    private String askForUsername(AuthenticationService authService) {
        while (true) {
            String username = mainView.getUsername();

            try {
                authService.validateUsername(username);
                return username;
            } catch (IllegalArgumentException exception) {
                mainView.showMessage(exception.getMessage());
                mainView.showMessage("Please enter another username.");
            }
        }
    }

    private String askForEmail(AuthenticationService authService) {
        while (true) {
            String email = mainView.getEmail();

            try {
                authService.validateEmail(email);
                return email;
            } catch (IllegalArgumentException exception) {
                mainView.showMessage(exception.getMessage());
                mainView.showMessage("Please enter your email again.");
            }
        }
    }

    private String askForPhone(AuthenticationService authService) {
        while (true) {
            String phone = mainView.getPhone();

            try {
                authService.validatePhone(phone);
                return phone;
            } catch (IllegalArgumentException exception) {
                mainView.showMessage(exception.getMessage());
                mainView.showMessage("Please enter your phone number again.");
            }
        }
    }

    private String askForPassword(AuthenticationService authService) {
        while (true) {
            mainView.showPasswordRules();
            String password = mainView.getPassword();

            try {
                authService.validatePassword(password);
                return password;
            } catch (IllegalArgumentException exception) {
                mainView.showMessage(exception.getMessage());
                mainView.showMessage("Please enter your password again.");
            }
        }
    }

    private void loginCustomer() {
        String username = mainView.getUsername();
        String password = mainView.getPassword();

        Customer customer = authenticationController.login(
                username,
                password
        );

        if (customer == null) {
            return;
        }

        customerController.setCustomerId(customer.getUserId());
        customerController.start();

        authenticationController.logout();
    }
}