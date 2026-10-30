package factory;

import dao.BookingDAO;
import dao.CustomerDAO;
import dao.MovieDAO;
import dao.PaymentDAO;
import dao.ScreenDAO;
import dao.SeatDAO;
import dao.ShowDAO;
import dao.TheatreDAO;
import impl.BookingDataStorageDAO;
import impl.BookingJDBCDAO;
import impl.CustomerDataStorageDAO;
import impl.CustomerJDBCDAO;
import impl.MovieDataStorageDAO;
import impl.MovieJDBCDAO;
import impl.PaymentDataStorageDAO;
import impl.PaymentJDBCDAO;
import impl.ScreenDataStorageDAO;
import impl.ScreenJDBCDAO;
import impl.SeatDataStorageDAO;
import impl.SeatJDBCDAO;
import impl.ShowDataStorageDAO;
import impl.ShowJDBCDAO;
import impl.TheatreDataStorageDAO;
import impl.TheatreJDBCDAO;
import java.util.Locale;


public final class DAOFactory {

    private DAOFactory() {}

    public static DAOSet create() {
        String storageType = System.getProperty("db.type",
                System.getenv().getOrDefault("DB_TYPE", "inmemory"));

        storageType = storageType.trim().toLowerCase(Locale.ROOT);

        return switch (storageType) {
            case "inmemory" -> createInMemoryDAOs();
            case "postgres" -> createPostgresDAOs();
            default -> throw new IllegalArgumentException(
                    "Unsupported db.type: " + storageType
                            + ". Use 'inmemory' or 'postgres'.");
        };
    }

    private static DAOSet createInMemoryDAOs() {
        MovieDAO movieDAO = new MovieDataStorageDAO();
        TheatreDAO theatreDAO = new TheatreDataStorageDAO();
        CustomerDAO customerDAO = new CustomerDataStorageDAO();

        ScreenDAO screenDAO = new ScreenDataStorageDAO(theatreDAO);
        SeatDAO seatDAO = new SeatDataStorageDAO(screenDAO);
        ShowDAO showDAO = new ShowDataStorageDAO(movieDAO, screenDAO);
        BookingDAO bookingDAO = new BookingDataStorageDAO(customerDAO,
                showDAO, seatDAO);
        PaymentDAO paymentDAO = new PaymentDataStorageDAO(bookingDAO);

        return new DAOSet(movieDAO, theatreDAO, customerDAO, screenDAO,
                seatDAO, showDAO, bookingDAO, paymentDAO);
    }

    private static DAOSet createPostgresDAOs() {
        MovieDAO movieDAO = new MovieJDBCDAO();
        TheatreDAO theatreDAO = new TheatreJDBCDAO();
        CustomerDAO customerDAO = new CustomerJDBCDAO();
        ScreenDAO screenDAO = new ScreenJDBCDAO();
        SeatDAO seatDAO = new SeatJDBCDAO();
        ShowDAO showDAO = new ShowJDBCDAO();
        BookingDAO bookingDAO = new BookingJDBCDAO();
        PaymentDAO paymentDAO = new PaymentJDBCDAO();

        return new DAOSet(movieDAO, theatreDAO, customerDAO, screenDAO,
                seatDAO, showDAO, bookingDAO, paymentDAO);
    }


    public static final class DAOSet {

        private final MovieDAO movieDAO;
        private final TheatreDAO theatreDAO;
        private final CustomerDAO customerDAO;
        private final ScreenDAO screenDAO;
        private final SeatDAO seatDAO;
        private final ShowDAO showDAO;
        private final BookingDAO bookingDAO;
        private final PaymentDAO paymentDAO;


        private DAOSet(MovieDAO movieDAO, TheatreDAO theatreDAO,
                       CustomerDAO customerDAO, ScreenDAO screenDAO,
                       SeatDAO seatDAO, ShowDAO showDAO, BookingDAO bookingDAO,
                       PaymentDAO paymentDAO) {
            this.movieDAO = movieDAO;
            this.theatreDAO = theatreDAO;
            this.customerDAO = customerDAO;
            this.screenDAO = screenDAO;
            this.seatDAO = seatDAO;
            this.showDAO = showDAO;
            this.bookingDAO = bookingDAO;
            this.paymentDAO = paymentDAO;
        }

        public MovieDAO getMovieDAO() {
            return movieDAO;
        }

        public TheatreDAO getTheatreDAO() {
            return theatreDAO;
        }

        public CustomerDAO getCustomerDAO() {
            return customerDAO;
        }

        public ScreenDAO getScreenDAO() {
            return screenDAO;
        }

        public SeatDAO getSeatDAO() {
            return seatDAO;
        }

        public ShowDAO getShowDAO() {
            return showDAO;
        }

        public BookingDAO getBookingDAO() {
            return bookingDAO;
        }

        public PaymentDAO getPaymentDAO() {
            return paymentDAO;
        }
    }
}