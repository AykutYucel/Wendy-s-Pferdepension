package at.ac.tuwien.sepm.assignment.individual.fleet.service;

import at.ac.tuwien.sepm.assignment.individual.fleet.dao.BookingVehicleDAO;
import at.ac.tuwien.sepm.assignment.individual.fleet.dao.BookingVehicleDAOJDBC;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Booking;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.BookingVehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class SimpleBookingVehicleService implements BookingVehicleService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private BookingVehicleDAO bookingVehicleDAO = new BookingVehicleDAOJDBC();

    public void bookingVehicle(BookingVehicle bookingVehicle) throws ServiceException {
        LOG.info("BookingVehicle service booking vehicle.");
        try {
            bookingVehicleDAO.bookingVehicle(bookingVehicle);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<BookingVehicle> findBookingVehicle(Booking booking) throws ServiceException{
        LOG.info("BookingVehicle service find booking vehicle.");
        try {
            return bookingVehicleDAO.find(booking);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void deleteBookingVehicle(Booking booking) throws ServiceException{
        LOG.info("BookingVehicle service delete booking vehicle.");
        try {
            bookingVehicleDAO.delete(booking);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public BookingVehicle getBookingVehicle(int bookingId, int vehicleId) throws ServiceException{
        LOG.info("BookingVehicle service get booking vehicle.");
        try {
            return bookingVehicleDAO.getBookingVehicle(bookingId, vehicleId);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void deleteBookingVehicle(BookingVehicle bookingVehicle) throws ServiceException{
        LOG.info("BookingVehicle service delete booking vehicle.");
        try {
            bookingVehicleDAO.delete(bookingVehicle);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }
    }
}
