package model;

public class Seat {
    private int seatId;
    private String seatNumber;
    private String seatRow;
    private String seatType;

    public Seat(int seatId, String seatNumber, String seatRow, String seatType) {
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.seatRow = seatRow;
        this.seatType = seatType;
    }

    public boolean isPremium(){
        return this.seatType.equalsIgnoreCase("PREMIUM");
    }

    public String getSeatLabel(){
        return seatNumber;
    }

    public int getSeatId() {
        return seatId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getSeatRow() {
        return seatRow;
    }

    public String getSeatType() {
        return seatType;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatId=" + seatId +
                ", seatNumber='" + seatNumber + '\'' +
                ", seatRow='" + seatRow + '\'' +
                ", seatType='" + seatType + '\'' +
                '}';
    }
}
