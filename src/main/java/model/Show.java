package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Show {
    private int showId;
    private LocalDate showDate;
    private LocalTime showTime;
    private double baseTicketPrice;
    private Screen screen;
    private int movieId;

    public Show(int showId, LocalDate showDate, LocalTime showTime, double baseTicketPrice, Screen screen) {
        this.showId = showId;
        this.showDate = showDate;
        this.showTime = showTime;
        this.baseTicketPrice = baseTicketPrice;
        this.screen = screen;
    }

    public boolean isUpcoming(){
        LocalDateTime showDateTime = LocalDateTime.of(showDate , showTime);
        return showDateTime.isAfter(LocalDateTime.now());
    }

    public double calculateTicketPrice(int seatCount){
        return baseTicketPrice * seatCount;
    }

    public List<Seat> getAvailableSeats(){
        return List.of();
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public int getShowId() {
        return showId;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public LocalTime getShowTime() {
        return showTime;
    }

    public double getBaseTicketPrice() {
        return baseTicketPrice;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    @Override
    public String toString() {
        return "Show{" +
                "showId=" + showId +
                ", showDate=" + showDate +
                ", showTime=" + showTime +
                ", baseTicketPrice=" + baseTicketPrice +
                ", movieId=" + movieId +
                '}';
    }
}
