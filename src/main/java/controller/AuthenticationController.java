package controller;

import model.Customer;
import service.AuthenticationService;
import view.MainView;

public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final MainView mainView;

    public AuthenticationController(
            AuthenticationService authenticationService,
            MainView mainView
    ) {
        this.authenticationService = authenticationService;
        this.mainView = mainView;
    }

    public Customer login(String username, String password) {
        try {
            Customer customer =
                    authenticationService.login(username, password);

            mainView.showMessage(
                    "Welcome back, " + customer.getName() + "!"
            );

            return customer;

        } catch (IllegalArgumentException exception) {
            mainView.showMessage(exception.getMessage());
            return null;
        }
    }

    public Customer register(
            int customerId,
            String name,
            String username,
            String email,
            String phone,
            String password
    ) {
        try {
            Customer customer = authenticationService.register(
                    customerId, name, username, email, phone, password
            );

            mainView.showMessage(
                    "Registration successful! Welcome, "
                            + customer.getName() + "!"
            );

            return customer;

        } catch (IllegalArgumentException exception) {
            mainView.showMessage(exception.getMessage());
            return null;
        }
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public void logout() {
        authenticationService.logout();
        mainView.showMessage("You have logged out successfully.");
    }
}