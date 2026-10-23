package app;

import controller.AdminController;
import controller.MainController;

import dao.*;
import model.*;
import storage.MovieDataStorage;
import view.AdminView;
import view.CustomerView;
import view.MainView;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.Scanner;

public class MovieTicketBookingApp {

    public static void main(String[] args) {
        MovieDataStorage storage = new MovieDataStorage();

        MovieDAO movieDAO = new MovieDataStorageDAO(storage);
        ShowDAO showDAO = new ShowDataStorageDAO(storage);
        BookingDAO bookingDAO = new BookingDataStorageDAO(storage);
        SeatDAO seatDAO = new SeatDataStorageDAO(storage);


        Scanner scanner = new Scanner(System.in);

        MainView mainView = new MainView(scanner);
        CustomerView customerView = new CustomerView(scanner);
        AdminView adminView = new AdminView(scanner);

        storage.movies.put(1,
                new Movie(1, "Inception", "Sci-Fi", "English", 148));

        storage.movies.put(2,
                new Movie(2, "Interstellar", "Sci-Fi", "English", 169));

        storage.movies.put(3,
                new Movie(3, "Vikram", "Action", "Tamil", 174));

        // Create the DAOs using the same storage
        TheatreDAO theatreDAO = new TheatreDataStorageDAO(storage);
        ScreenDAO screenDAO = new ScreenDataStorageDAO(storage);

        Theatre theatre = new Theatre(1, "PVR Cinemas", "Tirunelveli");
        theatreDAO.add(theatre);

        Screen screen = new Screen(1, "Screen 1", 50);
        screen.setTheatreId(theatre.getTheatreId());
        screenDAO.add(screen, theatre.getTheatreId());

        theatre.addScreen(screen);

        Show show = new Show(
                1,
                LocalDate.of(2026, 10, 20),
                LocalTime.of(18, 30),
                200.0,
                screen
        );

        showDAO.add(show, 1, 1);

        Seat seat1 = new Seat(1, "A1", "A", "STANDARD");
        Seat seat2 = new Seat(2, "A2", "A", "STANDARD");
        Seat seat3 = new Seat(3, "A3", "A", "PREMIUM");

        seatDAO.add(seat1, 1);
        seatDAO.add(seat2, 1);
        seatDAO.add(seat3, 1);

        screen.addSeat(seat1);
        screen.addSeat(seat2);
        screen.addSeat(seat3);

        Customer customer = new Customer(
                1,
                "Dany",
                "dany@example.com",
                "9876543210"
        );

        CustomerDAO customerDAO = new CustomerDataStorageDAO(storage);
        customerDAO.add(customer);


        Customer secondCustomer = new Customer(
                2,
                "Zapz",
                "test@example.com",
                "9876543211"
        );

        customerDAO.add(secondCustomer);

        PaymentDAO paymentDAO = new PaymentDataStorageDAO(storage);

        AdminController adminController =
                new AdminController(adminView);

        MainController mainController = new MainController(
                mainView,
                customerView,
                adminController,
                movieDAO,
                showDAO,
                bookingDAO,
                seatDAO,
                paymentDAO
        );

        mainController.start();
    }
}