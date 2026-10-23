package dao;

import model.Customer;
import storage.MovieDataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDataStorageDAO implements CustomerDAO {
    private final MovieDataStorage storage;

    public CustomerDataStorageDAO(MovieDataStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean add(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        int customerId = customer.getUserId();
        if (storage.customers.containsKey(customerId)) {
            throw new IllegalArgumentException("Customer ID already exists");
        }

        storage.customers.put(customerId, customer);
        return true;
    }

    @Override
    public boolean update(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        int customerId = customer.getUserId();
        if (!storage.customers.containsKey(customerId)) {
            throw new IllegalArgumentException("Customer not found");
        }

        storage.customers.put(customerId, customer);
        return true;
    }

    @Override
    public boolean deleteById(int customerId) {
        if (storage.customers.remove(customerId) == null) {
            throw new IllegalArgumentException("Customer not found");
        }
        return true;
    }

    @Override
    public Optional<Customer> findById(int customerId) {
        Customer customer = storage.customers.get(customerId);
        if (customer == null) {
            return Optional.empty();
        }
        return Optional.of(customer);
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(storage.customers.values());
    }
}