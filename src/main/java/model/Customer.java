package model;

import java.util.List;

public class Customer extends User {

    private String username;
    private String password;

    public Customer(
            int userId,
            String name,
            String username,
            String email,
            String phone,
            String password) {
        super(userId, name, email, phone);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Booking> getBookingHistory() {
        return List.of();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
