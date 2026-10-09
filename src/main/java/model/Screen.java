package model;

import java.util.ArrayList;
import java.util.List;

public class Screen {
    private int screenId;
    private String screenName;
    private int capacity;
    private List<Seat> seats;

    public Screen(int screenId, String screenName, int capacity) {
        this.screenId = screenId;
        this.screenName = screenName;
        this.capacity = capacity;
        this.seats = new ArrayList<>();
    }

    public void addSeat(Seat seat){
        seats.add(seat);
    }

    public void removeSeat(Seat seat){
        seats.remove(seat);
    }

    public int getScreenId() {
        return screenId;
    }

    public String getScreenName() {
        return screenName;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    @Override
    public String toString() {
        return "Screen{" +
                "screenId=" + screenId +
                ", screenName='" + screenName + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}
