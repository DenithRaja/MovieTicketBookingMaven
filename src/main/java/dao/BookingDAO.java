package dao;

import model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingDAO {
    boolean createBooking(Booking booking, int customerId, int showId, List<Integer> seatIds);
    Optional<Booking> findById(int bookingId);
    List<Booking> findByCustomerId(int customerId);
    List<Booking> findByShowId(int showId);
    List<Booking> findAll();
    boolean cancelBooking(int bookingId);
}