import model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ModelTest {

    public static void main(String[] args) {

        // 1. Create seats
        Seat seat1 = new Seat(1, "A1", "A", "REGULAR");
        Seat seat2 = new Seat(2, "A2", "A", "PREMIUM");
        Seat seat3 = new Seat(3, "A3", "A", "REGULAR");

        System.out.println("Seat 1 label: " + seat1.getSeatLabel());
        System.out.println("Seat 2 is premium: " + seat2.isPremium());


        // 2. Create screen and add seats
        Screen screen = new Screen(1, "Audit 1", 100);

        screen.addSeat(seat1);
        screen.addSeat(seat2);
        screen.addSeat(seat3);

        System.out.println("Number of seats: " + screen.getSeats().size());


        // 3. Create theatre and add screen
        Theatre theatre = new Theatre(1, "PVR", "Nagercoil");

        theatre.addScreen(screen);

        System.out.println("Number of screens: " + theatre.getScreenCount());
        System.out.println("Location matches: " + theatre.matchesLocation("nagercoil"));


        // 4. Create movie
        Movie movie = new Movie(
                1,
                "Interstellar",
                "Sci-Fi",
                "English",
                169
        );

        System.out.println("Genre matches: " + movie.matchesGenre("sci-fi"));
        System.out.println("Language matches: " + movie.matchesLanguage("ENGLISH"));


        // 5. Create show
        Show show = new Show(
                1,
                LocalDate.now().plusDays(1),
                LocalTime.of(19, 30),
                250,
                screen
        );

        System.out.println("Show is upcoming: " + show.isUpcoming());
        System.out.println("Price for 3 seats: " + show.calculateTicketPrice(3));
        System.out.println("Show's screen: " + show.getScreen());


        // 6. Create booking
        Booking booking = new Booking(
                1,
                LocalDateTime.now(),
                0,
                "PENDING",
                show
        );

        System.out.println("Booking's show: " + booking.getShow());


        // 7. Create BookingSeats
        BookingSeat bookingSeat1 = new BookingSeat(
                1,
                250,
                "PENDING",
                seat1
        );

        BookingSeat bookingSeat2 = new BookingSeat(
                2,
                250,
                "PENDING",
                seat2
        );

        booking.addBookingSeat(bookingSeat1);
        booking.addBookingSeat(bookingSeat2);

        System.out.println("Seat 1 in booking: " +
                bookingSeat1.getSeat().getSeatLabel());

        System.out.println("Seat 2 in booking: " +
                bookingSeat2.getSeat().getSeatLabel());


        // 8. Test BookingSeat
        bookingSeat1.markBooked();

        System.out.println("BookingSeat 1 booked: " +
                bookingSeat1.isBooked());

        bookingSeat1.cancel();

        System.out.println("BookingSeat 1 booked after cancel: " +
                bookingSeat1.isBooked());


        // 9. Calculate booking total
        System.out.println("Booking total: " +
                booking.calculateTotal());


        // 10. Confirm booking
        booking.confirmBooking();

        System.out.println("Booking confirmed: " +
                booking.isConfirmed());


        // 11. Create payment
        Payment payment = new Payment(
                1,
                500,
                "GPay",
                "PENDING"
        );

        System.out.println("Payment valid: " +
                payment.validatePaymentDetails());

        System.out.println("Payment processed: " +
                payment.processPayment());

        System.out.println("Payment status: " +
                payment.verifyPaymentStatus());


        // 12. Refund payment
        System.out.println("Refund successful: " +
                payment.refundPayment(500));

        System.out.println("Payment status after refund: " +
                payment.verifyPaymentStatus());
    }
}