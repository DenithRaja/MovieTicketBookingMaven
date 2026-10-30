package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private int bookingId;
    private LocalDateTime bookingDate;
    private double totalAmount;
    private String status;
    private List<BookingSeat> bookingSeats;
    private Show show;

    public Booking(int bookingId, LocalDateTime bookingDate, double totalAmount, String status, Show show) {
        this.bookingId = bookingId;
        this.bookingDate = bookingDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.bookingSeats = new ArrayList<>();
        this.show = show;
    }

    public Booking(
            LocalDateTime bookingDate,
            double totalAmount,
            String status,
            Show show) {

        this(0, bookingDate, totalAmount, status, show);
    }

    public void confirmBooking(){
        status = "BOOKED";
    }

    public void cancelBooking(){
        status = "CANCELLED";
    }

    public boolean isConfirmed(){
        return this.status.equalsIgnoreCase("BOOKED");
    }

    public void addBookingSeat(BookingSeat bookingSeat) {
        bookingSeats.add(bookingSeat);
    }

    public double   calculateTotal(){
        double total = 0;
        for (BookingSeat bookingSeat : bookingSeats){
            total += bookingSeat.getPrice();
        }
        totalAmount = total;
        return total;
    }

    public Show getShow() {
        return show;
    }

    public int getBookingId() {
        return bookingId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public List<BookingSeat> getBookingSeats() {
        return bookingSeats;
    }


    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public void setShow(Show show) {
        this.show = show;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", bookingDate=" + bookingDate +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", show=" + show +
                '}';
    }
}
