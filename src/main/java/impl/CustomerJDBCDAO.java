package impl;

import dao.CustomerDAO;
import model.Customer;
import util.DBConnection;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerJDBCDAO implements CustomerDAO {

    @Override
    public boolean add(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer cannot be null."
            );
        }

        String userSql = """
                INSERT INTO users (name, email, phone)
                VALUES (?, ?, ?)
                RETURNING user_id
                """;

        String customerSql = """
                INSERT INTO customers
                (customer_id, username, password_hash)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                int generatedUserId;

                try (PreparedStatement statement =
                             connection.prepareStatement(userSql)) {

                    statement.setString(1, customer.getName());
                    statement.setString(2, customer.getEmail());
                    statement.setString(3, customer.getPhone());

                    try (var resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new SQLException(
                                    "Failed to generate customer ID."
                            );
                        }

                        generatedUserId =
                                resultSet.getInt("user_id");
                    }
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(customerSql)) {

                    statement.setInt(1, generatedUserId);
                    statement.setString(
                            2, customer.getUsername()
                    );
                    statement.setString(
                            3,
                            PasswordUtil.hashPassword(
                                    customer.getPassword()
                            )
                    );

                    statement.executeUpdate();
                }

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }

                throw exception;

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to add customer to database.",
                    exception
            );
        }
    }

    @Override
    public boolean update(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer cannot be null."
            );
        }

        String userSql = """
                UPDATE users
                SET name = ?, email = ?, phone = ?
                WHERE user_id = ?
                """;

        String customerSql = """
                UPDATE customers
                SET username = ?
                WHERE customer_id = ?
                """;

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                int userRows;

                try (PreparedStatement statement =
                             connection.prepareStatement(userSql)) {

                    statement.setString(1, customer.getName());
                    statement.setString(2, customer.getEmail());
                    statement.setString(3, customer.getPhone());
                    statement.setInt(4, customer.getUserId());

                    userRows = statement.executeUpdate();
                }

                int customerRows;

                try (PreparedStatement statement =
                             connection.prepareStatement(customerSql)) {

                    statement.setString(
                            1, customer.getUsername()
                    );
                    statement.setInt(
                            2, customer.getUserId()
                    );

                    customerRows = statement.executeUpdate();
                }

                if (userRows == 1 && customerRows == 1) {
                    connection.commit();
                    return true;
                }

                connection.rollback();
                return false;

            } catch (SQLException exception) {
                connection.rollback();
                throw exception;

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to update customer.",
                    exception
            );
        }
    }

    @Override
    public boolean deleteById(int customerId) {
        String customerSql = """
                DELETE FROM customers
                WHERE customer_id = ?
                """;

        String userSql = """
                DELETE FROM users
                WHERE user_id = ?
                """;

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                int customerRows;

                try (PreparedStatement statement =
                             connection.prepareStatement(customerSql)) {

                    statement.setInt(1, customerId);
                    customerRows = statement.executeUpdate();
                }

                if (customerRows != 1) {
                    connection.rollback();
                    return false;
                }

                int userRows;

                try (PreparedStatement statement =
                             connection.prepareStatement(userSql)) {

                    statement.setInt(1, customerId);
                    userRows = statement.executeUpdate();
                }

                if (userRows == 1) {
                    connection.commit();
                    return true;
                }

                connection.rollback();
                return false;

            } catch (SQLException exception) {
                connection.rollback();
                throw exception;

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to delete customer.",
                    exception
            );
        }
    }

    @Override
    public Optional<Customer> findById(int customerId) {
        String sql = """
                SELECT u.user_id, u.name, u.email, u.phone,
                       c.username, c.password_hash
                FROM users u
                JOIN customers c ON u.user_id = c.customer_id
                WHERE c.customer_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, customerId);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapCustomer(resultSet));
                }

                return Optional.empty();
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find customer by ID.",
                    exception
            );
        }
    }

    @Override
    public List<Customer> findAll() {
        String sql = """
                SELECT u.user_id, u.name, u.email, u.phone,
                       c.username, c.password_hash
                FROM users u
                JOIN customers c ON u.user_id = c.customer_id
                ORDER BY u.user_id
                """;

        List<Customer> customers = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                customers.add(mapCustomer(resultSet));
            }

            return customers;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to retrieve all customers.",
                    exception
            );
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email cannot be empty."
            );
        }

        String sql = """
                SELECT u.user_id, u.name, u.email, u.phone,
                       c.username, c.password_hash
                FROM users u
                JOIN customers c ON u.user_id = c.customer_id
                WHERE LOWER(u.email) = LOWER(?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, email.trim());

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapCustomer(resultSet));
                }

                return Optional.empty();
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find customer by email.",
                    exception
            );
        }
    }

    @Override
    public Optional<Customer> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty."
            );
        }

        String sql = """
                SELECT u.user_id, u.name, u.email, u.phone,
                       c.username, c.password_hash
                FROM users u
                JOIN customers c ON u.user_id = c.customer_id
                WHERE LOWER(c.username) = LOWER(?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username.trim());

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapCustomer(resultSet));
                }

                return Optional.empty();
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find customer by username.",
                    exception
            );
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty."
            );
        }

        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM customers
                    WHERE LOWER(username) = LOWER(?)
                )
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username.trim());

            try (var resultSet = statement.executeQuery()) {
                return resultSet.next()
                        && resultSet.getBoolean(1);
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to check username existence.",
                    exception
            );
        }
    }

    private Customer mapCustomer(java.sql.ResultSet resultSet)
            throws SQLException {

        return new Customer(
                resultSet.getInt("user_id"),
                resultSet.getString("name"),
                resultSet.getString("username"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                resultSet.getString("password_hash")
        );
    }
}