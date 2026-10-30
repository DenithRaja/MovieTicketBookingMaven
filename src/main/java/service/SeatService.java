package service;

import dao.BookingDAO;
import dao.ScreenDAO;
import dao.SeatDAO;
import dao.ShowDAO;
import model.Booking;
import model.BookingSeat;
import model.Seat;
import model.Show;

import java.util.List;
import java.util.Optional;

public class SeatService {

    private final SeatDAO seatDAO;
    private final ScreenDAO screenDAO;
    private final ShowDAO showDAO;
    private final BookingDAO bookingDAO;

    public SeatService(
            SeatDAO seatDAO,
            ScreenDAO screenDAO,
            ShowDAO showDAO,
            BookingDAO bookingDAO
    ) {
        this.seatDAO = seatDAO;
        this.screenDAO = screenDAO;
        this.showDAO = showDAO;
        this.bookingDAO = bookingDAO;
    }

    public boolean addSeat(Seat seat, int screenId) {
        validateSeatDetails(seat);
        validatePositiveId(screenId, "Screen ID");

        if (screenDAO.findById(screenId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Screen with ID " + screenId + " does not exist."
            );
        }

        return seatDAO.add(seat, screenId);
    }

    public boolean updateSeat(Seat seat) {
        validateSeatDetails(seat);

        int seatId = seat.getSeatId();

        Optional<Seat> existingSeat = seatDAO.findById(seatId);

        if (existingSeat.isEmpty()) {
            throw new IllegalArgumentException(
                    "Seat with ID " + seatId + " does not exist."
            );
        }

        if (isSeatCurrentlyBooked(seatId)) {
            throw new IllegalArgumentException(
                    "Cannot update a seat that is currently booked."
            );
        }

        return seatDAO.update(seat);
    }

    public boolean removeSeat(int seatId) {
        validatePositiveId(seatId, "Seat ID");

        Optional<Seat> existingSeat = seatDAO.findById(seatId);

        if (existingSeat.isEmpty()) {
            throw new IllegalArgumentException(
                    "Seat with ID " + seatId + " does not exist."
            );
        }

        if (isSeatCurrentlyBooked(seatId)) {
            throw new IllegalArgumentException(
                    "Cannot remove a seat that is currently booked."
            );
        }

        for (Show show : showDAO.findAll()) {
            if (show.getScreen() == null) {
                continue;
            }

            int screenId = show.getScreen().getScreenId();

            boolean seatBelongsToScreen = seatDAO
                    .findByScreenId(screenId)
                    .stream()
                    .anyMatch(seat -> seat.getSeatId() == seatId);

            if (seatBelongsToScreen) {
                throw new IllegalArgumentException(
                        "Cannot remove a seat while its screen has scheduled shows."
                );
            }
        }

        return seatDAO.deleteById(seatId);
    }

    public Optional<Seat> findSeatById(int seatId) {
        validatePositiveId(seatId, "Seat ID");
        return seatDAO.findById(seatId);
    }

    public List<Seat> findSeatsByScreenId(int screenId) {
        validatePositiveId(screenId, "Screen ID");

        if (screenDAO.findById(screenId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Screen with ID " + screenId + " does not exist."
            );
        }

        return seatDAO.findByScreenId(screenId);
    }

    private boolean isSeatCurrentlyBooked(int seatId) {
        for (Booking booking : bookingDAO.findAll()) {
            for (BookingSeat bookingSeat : booking.getBookingSeats()) {
                if (bookingSeat.getSeat() != null
                        && bookingSeat.getSeat().getSeatId() == seatId
                        && bookingSeat.isBooked()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void validateSeatDetails(Seat seat) {
        if (seat == null) {
            throw new IllegalArgumentException(
                    "Seat cannot be null."
            );
        }

        validatePositiveId(seat.getSeatId(), "Seat ID");

        if (seat.getSeatNumber() == null
                || seat.getSeatNumber().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Seat number cannot be empty."
            );
        }

        if (seat.getSeatRow() == null
                || seat.getSeatRow().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Seat row cannot be empty."
            );
        }

        if (seat.getSeatType() == null
                || seat.getSeatType().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Seat type cannot be empty."
            );
        }

        if (!seat.getSeatType().equalsIgnoreCase("STANDARD")
                && !seat.getSeatType().equalsIgnoreCase("PREMIUM")) {
            throw new IllegalArgumentException(
                    "Seat type must be STANDARD or PREMIUM."
            );
        }
    }

    private void validatePositiveId(int id, String fieldName) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero."
            );
        }
    }
}