package impl;

import dao.BookingDAO;
import model.Booking;
import model.BookingSeat;
import model.Screen;
import model.Seat;
import model.Show;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BookingJDBCDAO implements BookingDAO {

    private static final int ID_LOCK_KEY = 827341;

    @Override
    public boolean createBooking(
            Booking booking,
            int customerId,
            int showId,
            List<Integer> seatIds) {

        if (booking == null || seatIds == null || seatIds.isEmpty()) {
            return false;
        }

        if (customerId <= 0 || showId <= 0) {
            return false;
        }

        if (seatIds.stream().anyMatch(id -> id == null || id <= 0)) {
            return false;
        }

        Set<Integer> uniqueSeatIds = new HashSet<>(seatIds);

        if (uniqueSeatIds.size() != seatIds.size()) {
            return false;
        }

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                if (!customerExists(connection, customerId)) {
                    connection.rollback();
                    return false;
                }

                int screenId = lockAndGetScreenId(connection, showId);

                if (screenId <= 0) {
                    connection.rollback();
                    return false;
                }

                Show show = loadShow(connection, showId);

                if (show == null || show.getScreen() == null) {
                    connection.rollback();
                    return false;
                }

                List<Seat> selectedSeats = new ArrayList<>();

                for (int seatId : seatIds) {
                    Seat seat = loadSeatForScreen(
                            connection, seatId, screenId);

                    if (seat == null
                            || isSeatAlreadyBooked(
                            connection, showId, seatId)) {
                        connection.rollback();
                        return false;
                    }

                    selectedSeats.add(seat);
                }

                double ticketPrice = show.getBaseTicketPrice();

                if (!Double.isFinite(ticketPrice) || ticketPrice < 0) {
                    connection.rollback();
                    return false;
                }

                double totalAmount = ticketPrice * selectedSeats.size();

                if (!Double.isFinite(totalAmount)) {
                    connection.rollback();
                    return false;
                }

                LocalDateTime bookingDate = booking.getBookingDate();

                if (bookingDate == null) {
                    bookingDate = LocalDateTime.now();
                }

                acquireIdLock(connection);

                int bookingId = nextId(
                        connection, "bookings", "booking_id");

                if (bookingId <= 0) {
                    connection.rollback();
                    return false;
                }

                String bookingSql =
                        "INSERT INTO bookings " +
                                "(booking_id, booking_date, total_amount, status, " +
                                "customer_id, show_id) VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement statement =
                             connection.prepareStatement(bookingSql)) {

                    statement.setInt(1, bookingId);
                    statement.setTimestamp(
                            2, Timestamp.valueOf(bookingDate));
                    statement.setBigDecimal(
                            3, BigDecimal.valueOf(totalAmount));
                    statement.setString(4, "BOOKED");
                    statement.setInt(5, customerId);
                    statement.setInt(6, showId);

                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                String bookingSeatSql =
                        "INSERT INTO booking_seats " +
                                "(booking_seat_id, booking_id, seat_id, " +
                                "price, status, show_id) VALUES (?, ?, ?, ?, ?, ?)";

                List<BookingSeat> createdBookingSeats =
                        new ArrayList<>();


                int nextBookingSeatId = nextId(
                        connection,
                        "booking_seats",
                        "booking_seat_id"
                );

                try (PreparedStatement statement =
                             connection.prepareStatement(bookingSeatSql)) {

                    for (Seat seat : selectedSeats) {
                        int bookingSeatId = nextBookingSeatId++;

                        statement.setInt(1, bookingSeatId);
                        statement.setInt(2, bookingId);
                        statement.setInt(3, seat.getSeatId());
                        statement.setBigDecimal(
                                4, BigDecimal.valueOf(ticketPrice));
                        statement.setString(5, "BOOKED");
                        statement.setInt(6, showId);

                        statement.addBatch();

                        createdBookingSeats.add(
                                new BookingSeat(
                                        bookingSeatId,
                                        ticketPrice,
                                        "BOOKED",
                                        seat
                                )
                        );
                    }

                    statement.executeBatch();
                }

                connection.commit();

                booking.setBookingId(bookingId);
                booking.setBookingDate(bookingDate);
                booking.setTotalAmount(totalAmount);
                booking.setStatus("BOOKED");
                booking.setShow(show);

                booking.getBookingSeats().clear();

                for (BookingSeat bookingSeat : createdBookingSeats) {
                    booking.addBookingSeat(bookingSeat);
                }

                return true;

            } catch (SQLException | RuntimeException exception) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                exception.addSuppressed(rollbackException);
            }

            if (exception instanceof SQLException sqlException
                    && "23505".equals(sqlException.getSQLState())) {
                return false;
            }

            if (exception instanceof SQLException) {
                System.err.println(
                        "Database error while creating booking: "
                                + exception.getMessage()
                );
            } else {
                System.err.println(
                        "Unexpected error while creating booking: "
                                + exception.getMessage()
                );
            }

            return false;
        }


        } catch (SQLException exception) {
            System.err.println(
                    "Database connection error while creating booking: "
                            + exception.getMessage()
            );

            return false;
        }
    }

    @Override
    public Optional<Booking> findById(int bookingId) {
        if (bookingId <= 0) {
            return Optional.empty();
        }

        try (Connection connection = DBConnection.getConnection()) {
            return loadBooking(connection, bookingId);

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to find booking by ID: " + bookingId,
                    exception
            );
        }
    }

    @Override
    public List<Booking> findByCustomerId(int customerId) {
        if (customerId <= 0) {
            return new ArrayList<>();
        }

        return findBookings(
                "SELECT booking_id FROM bookings " +
                        "WHERE customer_id = ? ORDER BY booking_id",
                customerId);
    }

    @Override
    public List<Booking> findByShowId(int showId) {
        if (showId <= 0) {
            return new ArrayList<>();
        }

        return findBookings(
                "SELECT booking_id FROM bookings " +
                        "WHERE show_id = ? ORDER BY booking_id",
                showId);
    }

    @Override
    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        List<Integer> bookingIds = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT booking_id FROM bookings ORDER BY booking_id");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                bookingIds.add(resultSet.getInt("booking_id"));
            }

            for (int bookingId : bookingIds) {
                loadBooking(connection, bookingId)
                        .ifPresent(bookings::add);
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to retrieve all bookings.",
                    exception
            );
        }

        return bookings;
    }


    @Override
    public boolean cancelBooking(int bookingId) {
        if (bookingId <= 0) {
            return false;
        }

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {

                Integer showId = findShowId(connection, bookingId);

                if (showId == null) {
                    connection.rollback();
                    return false;
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     "SELECT show_id FROM shows "
                                             + "WHERE show_id = ? FOR UPDATE")) {

                    statement.setInt(1, showId);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     "SELECT status FROM bookings "
                                             + "WHERE booking_id = ? FOR UPDATE")) {

                    statement.setInt(1, bookingId);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()
                                || !"BOOKED".equalsIgnoreCase(
                                resultSet.getString("status"))) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     "UPDATE bookings "
                                             + "SET status = 'CANCELLED' "
                                             + "WHERE booking_id = ? "
                                             + "AND status = 'BOOKED'")) {

                    statement.setInt(1, bookingId);

                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     "UPDATE booking_seats "
                                             + "SET status = 'CANCELLED' "
                                             + "WHERE booking_id = ? "
                                             + "AND status = 'BOOKED'")) {

                    statement.setInt(1, bookingId);
                    statement.executeUpdate();
                }

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException exception) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }

                if (exception instanceof SQLException sqlException
                        && sqlException.getSQLState() != null
                        && sqlException.getSQLState().startsWith("23")) {
                    return false;
                }

                if (exception instanceof SQLException) {
                    System.err.println(
                            "Database error while cancelling booking: "
                                    + exception.getMessage()
                    );
                } else {
                    System.err.println(
                            "Unexpected error while cancelling booking: "
                                    + exception.getMessage()
                    );
                }

                return false;
            }

        } catch (SQLException exception) {
            System.err.println(
                    "Database connection error while cancelling booking: "
                            + exception.getMessage()
            );

            return false;
        }
    }


    private boolean customerExists(
            Connection connection,
            int customerId) throws SQLException {

        String sql = "SELECT 1 FROM customers WHERE customer_id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private Integer findShowId(
            Connection connection,
            int bookingId) throws SQLException {

        String sql = "SELECT show_id FROM bookings WHERE booking_id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? resultSet.getInt("show_id")
                        : null;
            }
        }
    }

    private int lockAndGetScreenId(
            Connection connection,
            int showId) throws SQLException {

        String sql =
                "SELECT screen_id FROM shows WHERE show_id = ? FOR UPDATE";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, showId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? resultSet.getInt("screen_id")
                        : -1;
            }
        }
    }

    private Show loadShow(
            Connection connection,
            int showId) throws SQLException {

        String sql =
                "SELECT s.show_id, s.show_date, s.show_time, " +
                        "s.base_ticket_price, sc.screen_id, sc.screen_name, " +
                        "sc.capacity " +
                        "FROM shows s " +
                        "JOIN screens sc ON sc.screen_id = s.screen_id " +
                        "WHERE s.show_id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, showId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                Screen screen = new Screen(
                        resultSet.getInt("screen_id"),
                        resultSet.getString("screen_name"),
                        resultSet.getInt("capacity"));

                LocalDate date =
                        resultSet.getDate("show_date").toLocalDate();

                LocalTime time =
                        resultSet.getTime("show_time").toLocalTime();

                return new Show(
                        resultSet.getInt("show_id"),
                        date,
                        time,
                        resultSet.getDouble("base_ticket_price"),
                        screen);
            }
        }
    }

    private Seat loadSeatForScreen(
            Connection connection,
            int seatId,
            int screenId) throws SQLException {

        String sql =
                "SELECT seat_id, seat_number, seat_row, seat_type " +
                        "FROM seats WHERE seat_id = ? AND screen_id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, seatId);
            statement.setInt(2, screenId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return new Seat(
                        resultSet.getInt("seat_id"),
                        resultSet.getString("seat_number"),
                        resultSet.getString("seat_row"),
                        resultSet.getString("seat_type"));
            }
        }
    }

    private boolean isSeatAlreadyBooked(
            Connection connection,
            int showId,
            int seatId) throws SQLException {

        String sql =
                "SELECT 1 FROM booking_seats " +
                        "WHERE show_id = ? AND seat_id = ? AND status = 'BOOKED'";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, showId);
            statement.setInt(2, seatId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void acquireIdLock(Connection connection)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             "SELECT pg_advisory_xact_lock(?)")) {

            statement.setInt(1, ID_LOCK_KEY);
            statement.execute();
        }
    }

    private int nextId(
            Connection connection,
            String tableName,
            String columnName) throws SQLException {

        if (!("bookings".equals(tableName)
                && "booking_id".equals(columnName))
                && !("booking_seats".equals(tableName)
                && "booking_seat_id".equals(columnName))) {

            throw new IllegalArgumentException("Invalid ID source");
        }

        String sql =
                "SELECT COALESCE(MAX(" + columnName + "), 0) + 1 FROM "
                        + tableName;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }

        throw new SQLException("Unable to generate ID");
    }

    private List<Booking> findBookings(String sql, int parameter) {
        List<Booking> bookings = new ArrayList<>();
        List<Integer> bookingIds = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, parameter);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookingIds.add(resultSet.getInt("booking_id"));
                }
            }

            for (int bookingId : bookingIds) {
                loadBooking(connection, bookingId)
                        .ifPresent(bookings::add);
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to retrieve bookings.",
                    exception
            );
        }

        return bookings;
    }

    private Optional<Booking> loadBooking(
            Connection connection,
            int bookingId) throws SQLException {

        String sql =
                "SELECT b.booking_id, b.booking_date, b.total_amount, " +
                        "b.status, b.show_id, s.show_date, s.show_time, " +
                        "s.base_ticket_price, sc.screen_id, sc.screen_name, " +
                        "sc.capacity " +
                        "FROM bookings b " +
                        "JOIN shows s ON s.show_id = b.show_id " +
                        "JOIN screens sc ON sc.screen_id = s.screen_id " +
                        "WHERE b.booking_id = ?";

        Booking booking;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                Screen screen = new Screen(
                        resultSet.getInt("screen_id"),
                        resultSet.getString("screen_name"),
                        resultSet.getInt("capacity"));

                Show show = new Show(
                        resultSet.getInt("show_id"),
                        resultSet.getDate("show_date").toLocalDate(),
                        resultSet.getTime("show_time").toLocalTime(),
                        resultSet.getDouble("base_ticket_price"),
                        screen);

                Timestamp timestamp =
                        resultSet.getTimestamp("booking_date");

                LocalDateTime bookingDate =
                        timestamp.toLocalDateTime();

                booking = new Booking(
                        resultSet.getInt("booking_id"),
                        bookingDate,
                        resultSet.getDouble("total_amount"),
                        resultSet.getString("status"),
                        show);
            }
        }

        String seatSql =
                "SELECT bs.booking_seat_id, bs.price, bs.status, " +
                        "se.seat_id, se.seat_number, se.seat_row, se.seat_type " +
                        "FROM booking_seats bs " +
                        "JOIN seats se ON se.seat_id = bs.seat_id " +
                        "WHERE bs.booking_id = ? " +
                        "ORDER BY bs.booking_seat_id";

        try (PreparedStatement statement =
                     connection.prepareStatement(seatSql)) {

            statement.setInt(1, bookingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Seat seat = new Seat(
                            resultSet.getInt("seat_id"),
                            resultSet.getString("seat_number"),
                            resultSet.getString("seat_row"),
                            resultSet.getString("seat_type"));

                    BookingSeat bookingSeat = new BookingSeat(
                            resultSet.getInt("booking_seat_id"),
                            resultSet.getDouble("price"),
                            resultSet.getString("status"),
                            seat);

                    booking.addBookingSeat(bookingSeat);
                }
            }
        }

        return Optional.of(booking);
    }
}