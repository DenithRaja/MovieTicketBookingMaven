
package dao;

import model.Customer;
import org.junit.jupiter.api.Test;
import storage.MovieDataStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDAOTest {

    @Test
    void checkAddCustomer() {
        MovieDataStorage storage = new MovieDataStorage();
        CustomerDAO customerDAO = new CustomerDataStorageDAO(storage);

        Customer customer = new Customer(1, "Zapz", "zapz@example.com", "9876543210");

        boolean success = customerDAO.add(customer);

        assertTrue(success);
        assertTrue(storage.customers.containsKey(1));
        assertEquals(customer, storage.customers.get(1));
    }

    @Test
    void checkFindCustomerById(){
        MovieDataStorage storage = new MovieDataStorage();
        CustomerDAO customerDAO = new CustomerDataStorageDAO(storage);

        Customer customer = new Customer(1, "Zapz", "zapz@example.com", "9876543210");

        customerDAO.add(customer);

        assertTrue(customerDAO.findById(1).isPresent());
        assertEquals(customer, customerDAO.findById(1).get());
    }

    @Test
    void checkFindAllCustomers(){
        MovieDataStorage storage = new MovieDataStorage();
        CustomerDAO customerDAO = new CustomerDataStorageDAO(storage);

        Customer customer1 = new Customer(1, "Zapz", "zapz@example.com", "9876543210");

        Customer customer2 = new Customer(2, "Zeo", "zeo@example.com", "9876543211");

        customerDAO.add(customer1);
        customerDAO.add(customer2);

        List<Customer> customers = customerDAO.findAll();

        assertEquals(2,customers.size());
        assertTrue(customers.contains(customer1));
        assertTrue(customers.contains(customer2));
    }


    @Test
    void checkUpdateCustomer() {
        MovieDataStorage storage = new MovieDataStorage();
        CustomerDAO customerDAO = new CustomerDataStorageDAO(storage);

        Customer originalCustomer = new Customer(1, "Zapz", "zapz@example.com", "9876543210");

        customerDAO.add(originalCustomer);

        Customer updatedCustomer = new Customer(1, "Zapz Valo", "zapzvalo@example.com", "9876543212");

        boolean success = customerDAO.update(updatedCustomer);

        Customer result = customerDAO.findById(1).get();

        assertTrue(success);
        assertEquals("Zapz Valo", result.getName());
        assertEquals("zapzvalo@example.com", result.getEmail());
        assertEquals("9876543212", result.getPhone());
    }

    @Test
    void checkDeleteCustomerById(){
        MovieDataStorage storage = new MovieDataStorage();
        CustomerDAO customerDAO = new CustomerDataStorageDAO(storage);

        Customer customer = new Customer(1, "Zapz", "zapz@example.com", "9876543210");
        customerDAO.add(customer);

        boolean success = customerDAO.deleteById(1);

        assertTrue(success);
        assertFalse(storage.customers.containsKey(1));
    }

}
