package dao;

import model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {
    boolean add(Customer customer);
    boolean update(Customer customer);
    boolean deleteById(int customerId);
    Optional<Customer> findById(int customerId);
    List<Customer> findAll();
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByUsername(String username);
    boolean existsByUsername(String username);
}