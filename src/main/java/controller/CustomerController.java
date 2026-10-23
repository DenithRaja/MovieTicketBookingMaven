
package controller;

import dao.*;
import model.Booking;
import model.Movie;
import model.Seat;
import view.CustomerView;
import model.Show;
import model.Payment;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;;

public class CustomerController {

    private final CustomerView customerView;
    private final MovieDAO movieDAO;
    private final ShowDAO showDAO;
    private final BookingDAO bookingDAO;
    private final SeatDAO seatDAO;
    private final PaymentDAO paymentDAO;
    private final int customerId;
    private static final boolean SIMULATE_PAYMENT_FAILURE = true;

    private int nextBookingId = 1;

    public CustomerController(CustomerView customerView, MovieDAO movieDAO, ShowDAO showDAO, BookingDAO bookingDAO, SeatDAO seatDAO, PaymentDAO paymentDAO, int customerId) {
        this.customerView = customerView;
        this.movieDAO = movieDAO;
        this.showDAO = showDAO;
        this.bookingDAO = bookingDAO;
        this.seatDAO = seatDAO;
        this.paymentDAO = paymentDAO;
        this.customerId = customerId;
    }

    public void start() {
        while (true) {
            customerView.showCustomerMenu();

            int choice = customerView.getChoice();

            switch (choice) {
                case 1:
                    viewMovies();
                    break;

                case 2:
                    searchMovies();
                    break;

                case 3:
                    viewShows();
                    break;

                case 4:
                    bookTicket();
                    break;

                case 5:
                    viewMyBookings();
                    break;

                case 6:
                    cancelBooking();
                    break;

                case 7:
                    customerView.showMessage("Logging out...");
                    return;

                default:
                    customerView.showMessage(
                            "Invalid choice. Please select 1-7."
                    );
            }
        }
    }

    private void viewMovies() {
        List<Movie> movies = movieDAO.findAll();

        if (movies.isEmpty()) {
            customerView.showMessage("No movies available.");
            return;
        }

        for (Movie movie : movies) {
            customerView.showMessage(movie.toString());
        }
    }

    private void searchMovies() {
        String title = customerView.getMovieSearchText();

        List<Movie> movies = movieDAO.searchByTitle(title);

        if (movies.isEmpty()) {
            customerView.showMessage("No matching movies found.");
            return;
        }

        for (Movie movie : movies) {
            customerView.showMessage(movie.toString());
        }
    }

    private void viewShows() {
        List<Show> shows = showDAO.findAll();

        if (shows.isEmpty()) {
            customerView.showMessage("No shows available.");
            return;
        }

        for (Show show : shows) {
            int movieId = show.getMovieId();

            Movie movie = null;

            if (movieDAO.findById(movieId).isPresent()) {
                movie = movieDAO.findById(movieId).get();
            }

            if (movie != null) {
                customerView.showMessage(
                        "Show ID: " + show.getShowId()
                                + " | Movie: " + movie.getTitle()
                                + " | Date: " + show.getShowDate()
                                + " | Time: " + show.getShowTime()
                                + " | Price: ₹" + show.getBaseTicketPrice()
                );
            }
        }
    }

    private void bookTicket() {
        customerView.askShowId();
        int showId = customerView.getShowId();

        Show show = showDAO.findById(showId).orElse(null);

        if (show == null) {
            customerView.showMessage("Show not found.");
            return;
        }

        Movie movie = null;

        if (movieDAO.findById(show.getMovieId()).isPresent()) {
            movie = movieDAO.findById(show.getMovieId()).get();
        }

        if (movie != null) {
            customerView.showMessage("Selected Movie: " + movie.getTitle());
        }

        customerView.showMessage("Show ID: " + show.getShowId());
        customerView.showMessage("Date: " + show.getShowDate());
        customerView.showMessage("Time: " + show.getShowTime());
        customerView.showMessage("Ticket Price: ₹" + show.getBaseTicketPrice());

        List<Seat> seats =
                seatDAO.findByScreenId(show.getScreen().getScreenId());

        if (seats.isEmpty()) {
            customerView.showMessage("No seats available for this screen.");
            return;
        }

        customerView.showMessage("Seats in this screen:");

        for (Seat seat : seats) {
            customerView.showMessage(
                    seat.getSeatNumber() + " - " + seat.getSeatType()
            );
        }

        customerView.askSeatNumbers();
        List<String> selectedSeatNumbers = customerView.getSeatNumbers();

        if (selectedSeatNumbers.isEmpty()) {
            customerView.showMessage("Please select at least one seat.");
            return;
        }

        List<Integer> seatIds = new java.util.ArrayList<>();

        if (new HashSet<>(selectedSeatNumbers.stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .toList()).size() != selectedSeatNumbers.size()) {

            customerView.showMessage("Duplicate seat numbers are not allowed.");
            return;
        }

        for (String seatNumber : selectedSeatNumbers) {
            String CleanSeatNumber = seatNumber.trim();

            Seat selectedSeat = null;

            for (Seat seat : seats) {
                if (seat.getSeatNumber().equalsIgnoreCase(CleanSeatNumber)) {
                    selectedSeat = seat;
                    break;
                }
            }

            if (selectedSeat == null) {
                customerView.showMessage(
                        "Invalid seat number: " + CleanSeatNumber
                );
                return;
            }

            seatIds.add(selectedSeat.getSeatId());
        }

        String paymentMethod = customerView.getPaymentMethod();

        if (paymentMethod == null) {
            customerView.showMessage(
                    "Booking cancelled. Please select a valid payment method."
            );
            return;
        }

        int bookingId = nextBookingId++;

        Booking booking = new Booking(
                bookingId,
                LocalDateTime.now(),
                0.0,
                "BOOKED",
                show
        );

        boolean success = bookingDAO.createBooking(
                booking,
                customerId,
                show.getShowId(),
                seatIds
        );


        if (success) {
            int paymentId = paymentDAO.findAll().stream()
                    .mapToInt(Payment::getPaymentId)
                    .max()
                    .orElse(0) + 1;

            Payment payment = new Payment(
                    paymentId,
                    booking.getTotalAmount(),
                    paymentMethod,
                    "PENDING"
            );

            if (SIMULATE_PAYMENT_FAILURE) {
                payment.setSimulateFailure(true);
            }

            if (payment.processPayment()
                    && paymentDAO.add(payment, booking.getBookingId())) {

                customerView.showMessage("Booking successful!");
                customerView.showMessage("Booking ID: " + booking.getBookingId());
                customerView.showMessage("Total amount: ₹" + booking.getTotalAmount());
                customerView.showMessage("Payment ID: " + payment.getPaymentId());
                customerView.showMessage("Payment Method: " + payment.getPaymentMethod());
                customerView.showMessage("Payment Status: " + payment.getStatus());

            } else {
                boolean cancelled = bookingDAO.cancelBooking(booking.getBookingId());

                customerView.showMessage("Payment failed or could not be saved.");

                if (cancelled) {
                    customerView.showMessage("Booking cancelled successfully.");
                    customerView.showMessage("The seats are available again.");
                } else {
                    customerView.showMessage(
                            "Warning: Booking cancellation failed. Please check the booking status."
                    );
                }
            }

        } else {
            customerView.showMessage(
                    "Booking failed. One or more selected seats may already be booked."
            );
        }
    }
    private void viewMyBookings() {
        List<Booking> bookings = bookingDAO.findByCustomerId(customerId);

        if (bookings.isEmpty()) {
            customerView.showMessage("You have no bookings yet.");
            return;
        }
        customerView.showMessage("========== MY BOOKINGS ==========");

        for (Booking booking : bookings) {
            customerView.showMessage("Booking ID: " + booking.getBookingId());
            customerView.showMessage("Movie Show ID: " + booking.getShow().getShowId());
            customerView.showMessage(
                    "Booking Date: " +
                            booking.getBookingDate().format(
                                    java.time.format.DateTimeFormatter.ofPattern(
                                            "dd-MM-yyyy hh:mm a"
                                    )
                            )
            );
            customerView.showMessage("Total Amount: ₹" + booking.getTotalAmount());
            customerView.showMessage("Status: " + booking.getStatus());

            List<Payment> payments = paymentDAO.findByBookingId(booking.getBookingId());

            if (payments.isEmpty()) {
                customerView.showMessage("Payment: No payment record found.");
            } else {
                for (Payment payment : payments) {
                    customerView.showMessage("Payment ID: " + payment.getPaymentId());
                    customerView.showMessage("Payment Method: " + payment.getPaymentMethod());
                    customerView.showMessage("Payment Status: " + payment.getStatus());
                    customerView.showMessage("Payment Amount: ₹" + payment.getAmount());
                }
            }

            customerView.showMessage("---------------------------------");
        }
    }

    private void cancelBooking() {
        customerView.showMessage("========== CANCEL BOOKING ==========");
        customerView.showMessage("Enter Booking ID to cancel:");

        int bookingId = customerView.getBookingId();

        Booking booking = bookingDAO.findById(bookingId).orElse(null);

        if (booking == null) {
            customerView.showMessage("Booking not found.");
            return;
        }

        if (!bookingDAO.findByCustomerId(customerId).contains(booking)) {
            customerView.showMessage("You cannot cancel another customer's booking.");
            return;
        }

        if (!booking.isConfirmed()) {
            customerView.showMessage("This booking is already cancelled or inactive.");
            return;
        }

        List<Payment> payments = paymentDAO.findByBookingId(bookingId);
        Payment payment = payments.isEmpty() ? null : payments.get(0);

        boolean cancelled = bookingDAO.cancelBooking(bookingId);

        if (!cancelled) {
            customerView.showMessage("Unable to cancel the booking.");
            return;
        }

        customerView.showMessage("Booking cancelled successfully!");
        customerView.showMessage("Booking ID: " + bookingId);
        customerView.showMessage("The seats are now available for booking.");

        if (payment == null) {
            customerView.showMessage("No payment record found for this booking.");
            return;
        }

        if (payment.refundPayment(payment.getAmount())) {
            if (paymentDAO.update(payment)) {
                customerView.showMessage("Refund Amount: ₹" + payment.getRefundAmount());
                customerView.showMessage("Payment Status: " + payment.getStatus());
            } else {
                customerView.showMessage("Warning: Refund processed, but payment update failed.");
            }
        } else {
            customerView.showMessage(
                    "Refund could not be processed. Payment Status: " + payment.getStatus()
            );
        }
    }

}
