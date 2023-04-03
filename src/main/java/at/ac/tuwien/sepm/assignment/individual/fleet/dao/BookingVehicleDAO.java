package at.ac.tuwien.sepm.assignment.individual.fleet.dao;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Booking;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.BookingVehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.PersistenceException;

import java.util.List;

public interface BookingVehicleDAO {

    /**
     * This method creates relation between booking and vehicle
     * @param bookingVehicle relation which needs to be created
     * @throws PersistenceException  if database is not available
     */
    void bookingVehicle(BookingVehicle bookingVehicle) throws PersistenceException;

    /**
     * This method returns the list which contains relations between booking and vehicle
     * @param booking Booking object which will be used to find relations
     * @return List that contains relations
     * @throws PersistenceException if database is not available
     */
    List<BookingVehicle> find(Booking booking) throws PersistenceException;

    /**
     * This method deletes relations between booking and vehicle
     * @param booking Booking object which will be used to find all relations
     * @throws PersistenceException if database is not available
     */
    void delete(Booking booking) throws PersistenceException;

    /**
     * This method deletes relation between booking and vehicle
     * @param bookingVehicle BookingVehicle object which will be used to find exact relation.
     * @throws PersistenceException if database is not available
     */
    void delete(BookingVehicle bookingVehicle) throws PersistenceException;

    /**
     * This method returns the relation between a booking and vehicle
     * @param bookingId Booking object which will be used to find relation
     * @param vehicleId Vehicle object which will be used to find relation
     * @return BookingVehicle
     * @throws PersistenceException if database is not available
     */
    BookingVehicle getBookingVehicle(int bookingId, int vehicleId) throws PersistenceException;
}
