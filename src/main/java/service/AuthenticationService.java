package service;

import dao.CustomerDAO;
import model.Customer;
import util.PasswordUtil;

import java.util.Locale;
import java.util.List;

public final class AuthenticationService {

    private Customer currentCustomer;
    private final CustomerDAO customerDAO;

    public AuthenticationService(CustomerDAO customerDAO) {
        if (customerDAO == null) {
            throw new IllegalArgumentException(
                    "CustomerDAO cannot be null."
            );
        }

        this.customerDAO = customerDAO;
    }

    public Customer register(
            int customerId,
            String name,
            String username,
            String email,
            String phone,
            String password) {

        validateName(name);
        validateUsernameFormat(username);
        validateEmailFormat(email);
        validatePhone(phone);
        validatePassword(password);

        String normalizedName = name.trim();
        String normalizedUsername = username.trim();
        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);
        String normalizedPhone = phone.trim();

        if (customerDAO.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        if (customerDAO.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException(
                    "This username is already taken."
            );
        }

        int assignedId = customerId;

        if (assignedId <= 0
                || customerDAO.findById(assignedId).isPresent()) {
            assignedId = generateNextCustomerId();
        }

        Customer customer = new Customer(
                assignedId,
                normalizedName,
                normalizedUsername,
                normalizedEmail,
                normalizedPhone,
                password
        );

        boolean added = customerDAO.add(customer);

        if (!added) {
            throw new IllegalStateException(
                    "Customer registration failed."
            );
        }

        return customerDAO.findByUsername(normalizedUsername)
                .orElseThrow(() -> new IllegalStateException(
                        "Customer was registered but could not be retrieved."
                ));
    }

    private int generateNextCustomerId() {
        List<Customer> customers = customerDAO.findAll();

        int maximumId = customers.stream()
                .mapToInt(Customer::getUserId)
                .max()
                .orElse(0);

        try {
            return Math.addExact(maximumId, 1);
        } catch (ArithmeticException exception) {
            throw new IllegalStateException(
                    "No more customer IDs are available.",
                    exception
            );
        }
    }

    public Customer login(String username, String password) {
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Username and password are required."
            );
        }

        Customer customer = customerDAO
                .findByUsername(username.trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid username or password."
                ));

        String storedPassword = customer.getPassword();

        if (storedPassword == null
                || !PasswordUtil.verifyPassword(
                password, storedPassword)) {
            throw new IllegalArgumentException(
                    "Invalid username or password."
            );
        }

        currentCustomer = customer;
        return currentCustomer;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void logout() {
        currentCustomer = null;
    }

    public void validateName(String name) {
        if (name == null || name.isBlank()
                || !name.trim().matches("^[a-zA-Z ]+$")) {
            throw new IllegalArgumentException(
                    "Name must contain only letters and spaces."
            );
        }
    }

    public void validateUsername(String username) {
        validateUsernameFormat(username);

        if (customerDAO.existsByUsername(username.trim())) {
            throw new IllegalArgumentException(
                    "This username is already taken."
            );
        }
    }

    private void validateUsernameFormat(String username) {
        if (username == null
                || !username.trim().matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException(
                    "Username must contain only letters and numbers."
            );
        }
    }

    public void validateEmail(String email) {
        validateEmailFormat(email);

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        if (customerDAO.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }
    }

    private void validateEmailFormat(String email) {
        if (email == null || email.isBlank()
                || !email.trim().matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "Enter a valid email address."
            );
        }
    }

    public void validatePhone(String phone) {
        if (phone == null
                || !phone.trim().matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException(
                    "Phone number must contain exactly 10 digits."
            );
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required."
            );
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters."
            );
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one uppercase letter."
            );
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one lowercase letter."
            );
        }

        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one digit."
            );
        }

        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one special character."
            );
        }
    }
}