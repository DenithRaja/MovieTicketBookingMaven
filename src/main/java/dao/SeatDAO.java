package dao;

import model.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatDAO {
    boolean add(Seat seat, int screenId);
    boolean update(Seat seat);
    boolean deleteById(int seatId);
    Optional<Seat> findById(int seatId);
    List<Seat> findByScreenId(int screenId);
}