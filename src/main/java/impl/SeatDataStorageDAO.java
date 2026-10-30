
package impl;

import dao.ScreenDAO;
import dao.SeatDAO;
import model.Seat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SeatDataStorageDAO implements SeatDAO {

    private final Map<Integer, Seat> seats = new LinkedHashMap<>();
    private final Map<Integer, Integer> seatScreenIds = new LinkedHashMap<>();

    private final ScreenDAO screenDAO;

    public SeatDataStorageDAO(ScreenDAO screenDAO) {
        this.screenDAO = screenDAO;
    }

    @Override
    public boolean add(Seat seat, int screenId) {
        if (seat == null) {
            throw new IllegalArgumentException("Seat cannot be null");
        }

        if (screenDAO.findById(screenId).isEmpty()) {
            throw new IllegalArgumentException("Screen not found");
        }

        int seatId = seat.getSeatId();

        if (seats.containsKey(seatId)) {
            throw new IllegalArgumentException("Seat ID already exists");
        }

        seats.put(seatId, seat);
        seatScreenIds.put(seatId, screenId);

        return true;
    }

    @Override
    public boolean update(Seat seat) {
        if (seat == null) {
            throw new IllegalArgumentException("Seat cannot be null");
        }

        int seatId = seat.getSeatId();

        if (!seats.containsKey(seatId)) {
            throw new IllegalArgumentException("Seat not found");
        }

        seats.put(seatId, seat);

        return true;
    }

    @Override
    public boolean deleteById(int seatId) {
        if (!seats.containsKey(seatId)) {
            throw new IllegalArgumentException("Seat not found");
        }

        seats.remove(seatId);
        seatScreenIds.remove(seatId);

        return true;
    }

    @Override
    public Optional<Seat> findById(int seatId) {
        return Optional.ofNullable(seats.get(seatId));
    }

    @Override
    public List<Seat> findByScreenId(int screenId) {
        List<Seat> matchingSeats = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : seatScreenIds.entrySet()) {
            if (entry.getValue() == screenId) {
                Seat seat = seats.get(entry.getKey());

                if (seat != null) {
                    matchingSeats.add(seat);
                }
            }
        }

        return matchingSeats;
    }
}
