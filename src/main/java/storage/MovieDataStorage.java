package storage;

import model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MovieDataStorage {


    public final Map<Integer, Movie> movies = new LinkedHashMap<>();
    public final Map<Integer, Theatre> theatres = new LinkedHashMap<>();
    public final Map<Integer, Screen> screens = new LinkedHashMap<>();
    public final Map<Integer, Seat> seats = new LinkedHashMap<>();
    public final Map<Integer, Show> shows = new LinkedHashMap<>();
    public final Map<Integer, Customer> customers = new LinkedHashMap<>();
    public final Map<Integer, Booking> bookings = new LinkedHashMap<>();
    public final Map<Integer, Payment> payments = new LinkedHashMap<>();

    public final Map<Integer, Integer> seatScreenIds = new LinkedHashMap<>();
    public final Map<Integer, Integer> bookingCustomerIds = new LinkedHashMap<>();
    public final Map<Integer, Integer> paymentBookingIds = new LinkedHashMap<>();

    public final List<BookingSeat> bookingSeats = new ArrayList<>();
}