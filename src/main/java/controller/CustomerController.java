package controller;

import dao.*;
import model.Booking;
import model.Movie;
import model.Payment;
import model.Seat;
import model.Show;
import service.BookingService;
import service.PaymentService;
import view.CustomerView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CustomerController {

    private final CustomerView customerView;
    private final MovieDAO movieDAO;
    private final ShowDAO showDAO;
    private final BookingDAO bookingDAO;
    private final SeatDAO seatDAO;
    private final BookingService bookingService;
    private final PaymentService paymentService;

    private int customerId;
    private static final boolean Failure_Test = false;

    public CustomerController(
            CustomerView customerView,
            MovieDAO movieDAO,
            ShowDAO showDAO,
            BookingDAO bookingDAO,
            SeatDAO seatDAO,
            BookingService bookingService,
            PaymentService paymentService
    ) {
        this.customerView = customerView;
        this.movieDAO = movieDAO;
        this.showDAO = showDAO;
        this.bookingDAO = bookingDAO;
        this.seatDAO = seatDAO;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
    }

    public void setCustomerId(int customerId) {
        if (customerId <= 0) {
            throw new IllegalArgumentException(
                    "Customer ID must be greater than zero."
            );
        }

        this.customerId = customerId;
    }

    public void start() {
        while (true) {
            customerView.showCustomerMenu();
            int choice = customerView.getChoice();

            switch (choice) {
                case 1 -> viewMovies();
                case 2 -> searchMovies();
                case 3 -> viewShows();
                case 4 -> bookTicket();
                case 5 -> viewMyBookings();
                case 6 -> cancelBooking();
                case 7 -> {
                    customerView.showMessage("Logging out...");
                    return;
                }
                default -> customerView.showMessage(
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
            Movie movie = movieDAO.findById(show.getMovieId())
                    .orElse(null);

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

        if (show.getScreen() == null) {
            customerView.showMessage(
                    "The selected show does not have a screen."
            );
            return;
        }

        Movie movie = movieDAO.findById(show.getMovieId())
                .orElse(null);

        if (movie != null) {
            customerView.showMessage(
                    "Selected Movie: " + movie.getTitle()
            );
        }

        customerView.showMessage("Show ID: " + show.getShowId());
        customerView.showMessage("Date: " + show.getShowDate());
        customerView.showMessage("Time: " + show.getShowTime());
        customerView.showMessage(
                "Ticket Price: ₹" + show.getBaseTicketPrice()
        );

        List<Seat> seats =
                seatDAO.findByScreenId(show.getScreen().getScreenId());

        if (seats.isEmpty()) {
            customerView.showMessage(
                    "No seats available for this screen."
            );
            return;
        }

        customerView.showMessage("Seats in this screen:");

        for (Seat seat : seats) {
            customerView.showMessage(
                    seat.getSeatNumber() + " - " + seat.getSeatType()
            );
        }

        customerView.askSeatNumbers();
        List<String> selectedSeatNumbers =
                customerView.getSeatNumbers();

        if (selectedSeatNumbers == null
                || selectedSeatNumbers.isEmpty()) {
            customerView.showMessage(
                    "Please select at least one seat."
            );
            return;
        }

        if (new HashSet<>(selectedSeatNumbers.stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .toList()).size() != selectedSeatNumbers.size()) {
            customerView.showMessage(
                    "Duplicate seat numbers are not allowed."
            );
            return;
        }

        List<Integer> seatIds = new ArrayList<>();

        for (String seatNumber : selectedSeatNumbers) {
            String cleanSeatNumber = seatNumber.trim();

            Seat selectedSeat = null;

            for (Seat seat : seats) {
                if (seat.getSeatNumber()
                        .equalsIgnoreCase(cleanSeatNumber)) {
                    selectedSeat = seat;
                    break;
                }
            }

            if (selectedSeat == null) {
                customerView.showMessage(
                        "Invalid seat number: " + cleanSeatNumber
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

        Booking booking = new Booking(
                LocalDateTime.now(),
                0.0,
                "BOOKED",
                show
        );

        boolean bookingCreated;

        try {
            bookingCreated = bookingService.createBooking(
                    booking, customerId, showId, seatIds
            );
        } catch (IllegalArgumentException exception) {
            customerView.showMessage(
                    "Booking failed: " + exception.getMessage()
            );
            return;
        }

        if (!bookingCreated) {
            customerView.showMessage(
                    "Booking failed. One or more selected seats may already be booked."
            );
            return;
        }

        Payment payment = new Payment(
                booking.getTotalAmount(),
                paymentMethod,
                "PENDING"
        );

        if (Failure_Test) {
            payment.setSimulateFailure(true);
        }

        boolean paymentSuccessful;

        try {
            paymentSuccessful = paymentService.processPayment(
                    payment, booking.getBookingId()
            );
        } catch (IllegalArgumentException
                 | IllegalStateException exception) {
            customerView.showMessage(
                    "Payment error: " + exception.getMessage()
            );
            paymentSuccessful = false;
        }

        if (paymentSuccessful) {
            customerView.showMessage("Booking successful!");
            customerView.showMessage(
                    "Booking ID: " + booking.getBookingId()
            );
            customerView.showMessage(
                    "Total amount: ₹" + booking.getTotalAmount()
            );
            customerView.showMessage(
                    "Payment ID: " + payment.getPaymentId()
            );
            customerView.showMessage(
                    "Payment Method: " + payment.getPaymentMethod()
            );
            customerView.showMessage(
                    "Payment Status: " + payment.getStatus()
            );
        } else {
            customerView.showMessage(
                    "Payment failed. The booking will be cancelled."
            );

            try {
                boolean cancelled = bookingService.cancelBooking(
                        booking.getBookingId()
                );

                if (cancelled) {
                    customerView.showMessage(
                            "Booking cancelled successfully."
                    );
                    customerView.showMessage(
                            "The seats are available again."
                    );
                } else {
                    customerView.showMessage(
                            "Warning: Booking cancellation failed."
                    );
                }
            } catch (IllegalArgumentException exception) {
                customerView.showMessage(
                        "Cancellation issue: " + exception.getMessage()
                );
            }
        }
    }

    private void viewMyBookings() {
        List<Booking> bookings;

        try {
            bookings =
                    bookingService.findBookingsByCustomerId(customerId);
        } catch (IllegalArgumentException exception) {
            customerView.showMessage(exception.getMessage());
            return;
        }

        if (bookings.isEmpty()) {
            customerView.showMessage("You have no bookings yet.");
            return;
        }

        customerView.showMessage("========== MY BOOKINGS ==========");

        for (Booking booking : bookings) {
            customerView.showMessage(
                    "Booking ID: " + booking.getBookingId()
            );

            if (booking.getShow() != null) {
                customerView.showMessage(
                        "Movie Show ID: "
                                + booking.getShow().getShowId()
                );
            }

            customerView.showMessage(
                    "Booking Date: "
                            + booking.getBookingDate().format(
                            java.time.format.DateTimeFormatter.ofPattern(
                                    "dd-MM-yyyy hh:mm a"
                            )
                    )
            );

            customerView.showMessage(
                    "Total Amount: ₹" + booking.getTotalAmount()
            );
            customerView.showMessage(
                    "Status: " + booking.getStatus()
            );

            List<Payment> payments;

            try {
                payments = paymentService.findPaymentsByBookingId(
                        booking.getBookingId()
                );
            } catch (IllegalArgumentException exception) {
                customerView.showMessage(exception.getMessage());
                continue;
            }

            if (payments.isEmpty()) {
                customerView.showMessage(
                        "Payment: No payment record found."
                );
            } else {
                for (Payment payment : payments) {
                    customerView.showMessage(
                            "Payment ID: " + payment.getPaymentId()
                    );
                    customerView.showMessage(
                            "Payment Method: " + payment.getPaymentMethod()
                    );
                    customerView.showMessage(
                            "Payment Status: " + payment.getStatus()
                    );
                    customerView.showMessage(
                            "Payment Amount: ₹" + payment.getAmount()
                    );
                }
            }

            customerView.showMessage(
                    "---------------------------------"
            );
        }
    }

    private void cancelBooking() {
        customerView.showMessage(
                "========== CANCEL BOOKING =========="
        );
        customerView.showMessage("Enter Booking ID to cancel:");

        int bookingId = customerView.getBookingId();

        Booking booking;

        try {
            booking = bookingService.findBookingById(bookingId)
                    .orElse(null);
        } catch (IllegalArgumentException exception) {
            customerView.showMessage(exception.getMessage());
            return;
        }

        if (booking == null) {
            customerView.showMessage("Booking not found.");
            return;
        }

        List<Booking> customerBookings;

        try {
            customerBookings =
                    bookingService.findBookingsByCustomerId(customerId);
        } catch (IllegalArgumentException exception) {
            customerView.showMessage(exception.getMessage());
            return;
        }

        boolean belongsToCustomer = customerBookings.stream()
                .anyMatch(customerBooking ->
                        customerBooking.getBookingId() == bookingId
                );

        if (!belongsToCustomer) {
            customerView.showMessage(
                    "You cannot cancel another customer's booking."
            );
            return;
        }

        if (!booking.isConfirmed()) {
            customerView.showMessage(
                    "This booking is already cancelled or inactive."
            );
            return;
        }

        List<Payment> payments;

        try {
            payments = paymentService.findPaymentsByBookingId(bookingId);
        } catch (IllegalArgumentException exception) {
            customerView.showMessage(exception.getMessage());
            return;
        }

        Payment payment = payments.isEmpty() ? null : payments.get(0);

        boolean cancelled;

        try {
            cancelled = bookingService.cancelBooking(bookingId);
        } catch (IllegalArgumentException exception) {
            customerView.showMessage(exception.getMessage());
            return;
        }

        if (!cancelled) {
            customerView.showMessage("Unable to cancel the booking.");
            return;
        }

        customerView.showMessage("Booking cancelled successfully!");
        customerView.showMessage("Booking ID: " + bookingId);
        customerView.showMessage(
                "The seats are now available for booking."
        );

        if (payment == null) {
            customerView.showMessage(
                    "No payment record found for this booking."
            );
            return;
        }

        if (paymentService.refundPayment(payment.getPaymentId())) {

            List<Payment> updatedPayments =
                    paymentService.findPaymentsByBookingId(bookingId);

            Payment updatedPayment = updatedPayments.stream()
                    .filter(p -> p.getPaymentId() == payment.getPaymentId())
                    .findFirst()
                    .orElse(null);

            if (updatedPayment != null) {
                customerView.showMessage(
                        "Refund Amount: ₹" +
                                updatedPayment.getRefundAmount()
                );

                customerView.showMessage(
                        "Payment Status: " +
                                updatedPayment.getStatus()
                );
            }

        } else {
            customerView.showMessage(
                    "Refund could not be processed. Payment Status: "
                            + payment.getStatus()
            );
        }
    }
}