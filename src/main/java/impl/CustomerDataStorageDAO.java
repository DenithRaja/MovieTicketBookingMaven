
package impl;

import dao.CustomerDAO;
import model.Customer;
import util.PasswordUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomerDataStorageDAO implements CustomerDAO {

    private final Map<Integer, Customer> customers = new LinkedHashMap<>();

    public CustomerDataStorageDAO() {
    }

    @Override
    public boolean add(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        int customerId = customer.getUserId();

        if (customers.containsKey(customerId)) {
            throw new IllegalArgumentException("Customer ID already exists");
        }

        Customer storedCustomer = new Customer(
                customer.getUserId(),
                customer.getName(),
                customer.getUsername(),
                customer.getEmail(),
                customer.getPhone(),
                PasswordUtil.hashPassword(customer.getPassword())
        );

        customers.put(customerId, storedCustomer);

        return true;
    }

    @Override
    public boolean update(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        int customerId = customer.getUserId();

        Customer existingCustomer = customers.get(customerId);

        if (existingCustomer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        Customer updatedCustomer = new Customer(
                customer.getUserId(),
                customer.getName(),
                customer.getUsername(),
                customer.getEmail(),
                customer.getPhone(),
                existingCustomer.getPassword()
        );

        customers.put(customerId, updatedCustomer);

        return true;
    }

    @Override
    public boolean deleteById(int customerId) {
        if (customers.remove(customerId) == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        return true;
    }

    @Override
    public Optional<Customer> findById(int customerId) {
        return Optional.ofNullable(customers.get(customerId));
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }

        return customers.values().stream()
                .filter(customer ->
                        customer.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }


    @Override
    public Optional<Customer> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty."
            );
        }

        return customers.values().stream()
                .filter(customer ->
                        customer.getUsername() != null
                                && customer.getUsername()
                                .equalsIgnoreCase(username.trim()))
                .findFirst();
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }
}
