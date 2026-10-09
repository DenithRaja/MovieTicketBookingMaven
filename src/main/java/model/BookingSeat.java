package model;

public class BookingSeat {
    private int bookingSeatId;
    private double price;
    private String status;
    private Seat seat;

    public BookingSeat(int bookingSeatId, double price, String status, Seat seat) {
        this.bookingSeatId = bookingSeatId;
        this.price = price;
        this.status = status;
        this.seat = seat;
    }

    public void markBooked(){
        status = "BOOKED";
    }

    public void cancel(){
        status = "CANCELLED";
    }

    public boolean isBooked(){
        return this.status.equalsIgnoreCase("BOOKED");
    }

    public double getPrice() {
        return price;
    }

    public Seat getSeat() {
        return seat;
    }

    public int getBookingSeatId() {
        return bookingSeatId;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "BookingSeat{" +
                "bookingSeatId=" + bookingSeatId +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", seat=" + seat +
                '}';
    }
}

