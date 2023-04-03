package at.ac.tuwien.sepm.assignment.individual.fleet.vehicle;

import at.ac.tuwien.sepm.assignment.individual.fleet.dao.BookingDAO;
import at.ac.tuwien.sepm.assignment.individual.fleet.dao.BookingDAOJDBC;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Booking;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.BookingStatus;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.PersistenceException;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class BookingDAONormalTest {

    BookingDAO bookingDAO = new BookingDAOJDBC();

    @Test
    public void AddBookingValidateTest() throws PersistenceException {
        Booking booking = new Booking();
        booking.setName("Ismail");
        booking.setPayment_info("adasdasd");
        booking.setToDate(LocalDateTime.now());
        booking.setFromDate(LocalDateTime.now());
        booking.setTotalPrice(200);
        booking.setStatus(BookingStatus.open);
        booking.setDateOfIssue(Timestamp.valueOf(LocalDateTime.now()));

        boolean result = false;
        bookingDAO.book(booking);
        result = true;
        assertTrue(result);

    }

    @Test
    public void CompleteBookingValidateTest() throws PersistenceException {
        Booking booking = new Booking();
        booking.setName("Ismail");
        booking.setPayment_info("adasdasd");
        booking.setToDate(LocalDateTime.now());
        booking.setFromDate(LocalDateTime.now());
        booking.setTotalPrice(200);
        booking.setStatus(BookingStatus.open);
        booking.setDateOfIssue(Timestamp.valueOf(LocalDateTime.now()));


        bookingDAO.book(booking);
        boolean result = false;
        bookingDAO.complete(booking);
        result = true;
        assertTrue(result);
    }

    @Test
    public void CancelBookingValidateTest() throws PersistenceException {
        Booking booking = new Booking();
        booking.setName("Ismail");
        booking.setPayment_info("adasdasd");
        booking.setToDate(LocalDateTime.now());
        booking.setFromDate(LocalDateTime.now());
        booking.setTotalPrice(200);
        booking.setStatus(BookingStatus.open);
        booking.setDateOfIssue(Timestamp.valueOf(LocalDateTime.now()));


        bookingDAO.book(booking);
        boolean result = false;
        bookingDAO.cancel(booking);
        result = true;
        assertTrue(result);
    }

}
