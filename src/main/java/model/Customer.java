package model;

import java.util.List;

public class Customer extends User{
    public Customer(int userId, String name, String email, String phone) {
        super(userId, name, email, phone);
    }

    public void addBooking(Booking booking){

    }

    public List<Booking> getBookingHistory(){
        return List.of();
    }



    @Override
    public String toString() {
        return "Customer{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
    