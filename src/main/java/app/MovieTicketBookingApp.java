package app;

import controller.AdminController;
import controller.AuthenticationController;
import controller.CustomerController;
import controller.MainController;
import dao.BookingDAO;
import dao.CustomerDAO;
import dao.MovieDAO;
import dao.PaymentDAO;
import dao.ScreenDAO;
import dao.SeatDAO;
import dao.ShowDAO;
import dao.TheatreDAO;
import factory.DAOFactory;
import factory.DAOFactory.DAOSet;
import java.util.Scanner;
import service.AuthenticationService;
import service.BookingService;
import service.MovieService;
import service.PaymentService;
import service.ScreenService;
import service.SeatService;
import service.ShowService;
import service.TheatreService;
import view.AdminView;
import view.CustomerView;
import view.MainView;


public class MovieTicketBookingApp {

    public static void main(String[] args) {
        DAOSet daos = DAOFactory.create();

        MovieDAO movieDAO = daos.getMovieDAO();
        TheatreDAO theatreDAO = daos.getTheatreDAO();
        CustomerDAO customerDAO = daos.getCustomerDAO();
        ScreenDAO screenDAO = daos.getScreenDAO();
        SeatDAO seatDAO = daos.getSeatDAO();
        ShowDAO showDAO = daos.getShowDAO();
        BookingDAO bookingDAO = daos.getBookingDAO();
        PaymentDAO paymentDAO = daos.getPaymentDAO();

        MovieService movieService = new MovieService(movieDAO, showDAO);
        ScreenService screenService = new ScreenService(screenDAO,
                theatreDAO, seatDAO, showDAO);
        SeatService seatService = new SeatService(seatDAO, screenDAO,
                showDAO, bookingDAO);
        ShowService showService = new ShowService(showDAO, bookingDAO,
                movieDAO, screenDAO);
        BookingService bookingService = new BookingService(bookingDAO,
                customerDAO, showDAO, seatDAO);
        TheatreService theatreService = new TheatreService(theatreDAO,
                screenDAO);
        PaymentService paymentService = new PaymentService(paymentDAO,
                bookingDAO);
        AuthenticationService authenticationService =
                new AuthenticationService(customerDAO);

        Scanner scanner = new Scanner(System.in);

        MainView mainView = new MainView(scanner);
        CustomerView customerView = new CustomerView(scanner);
        AdminView adminView = new AdminView(scanner);

        AdminController adminController = new AdminController(adminView,
                theatreService, screenService, seatService, movieService,
                showService);

        CustomerController customerController = new CustomerController(
                customerView, movieDAO, showDAO, bookingDAO, seatDAO,
                bookingService, paymentService);

        AuthenticationController authenticationController =
                new AuthenticationController(authenticationService, mainView);

        MainController mainController = new MainController(mainView,
                customerView, adminController, customerController,
                authenticationController, customerDAO);

        mainController.start();
    }
}