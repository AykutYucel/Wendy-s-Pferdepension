package at.ac.tuwien.sepm.assignment.individual.fleet.dao;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.*;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.VehicleNotAvailableInTimePeriodException;

import java.util.List;

public interface BookingDAO {
    /**
     * This method creates a booking in the database
     * @param booking Booking object to be inserted into the database
     * @throws PersistenceException if database is not available
     */
    void book(Booking booking) throws PersistenceException;

    /**
     *
     * @param booking Booking object to be inserted into the database
     * @param vehicleSearchFilter Filter object to create query
     * @param vehicle Vehicle object to book in period
     * @throws VehicleNotAvailableInTimePeriodException
     * @throws PersistenceException if database is not available
     */
    void book(Booking booking, VehicleSearchFilter vehicleSearchFilter, Vehicle vehicle) throws VehicleNotAvailableInTimePeriodException, PersistenceException;
    List<Booking> find(BookingSearchFilter bookingSearchFilter) throws PersistenceException;

    /**
     * This method returns chart information to fill the charts.
     * @param chartFilter Filter object to create query
     * @return Returns a list of ChartInformation
     * @throws PersistenceException if database is not available
     */
    List<ChartInformation> find(ChartFilter chartFilter) throws PersistenceException;

    /**
     * This method change the status of the booking.
     * @param booking Booking object which needs to be completed
     * @throws PersistenceException if database is not available
     */
    void complete(Booking booking) throws PersistenceException;

    /**
     * This method deletes booking if there is no cancellation fee
     * @param booking Booking object which needs to be deleted
     * @throws PersistenceException if database is not available
     */
    void delete(Booking booking) throws PersistenceException;

    /**
     * This method cancels booking if there is a cancellation fee
     * @param booking Booking object which needs to be deleted
     * @throws PersistenceException if database is not available
     */
    void cancel(Booking booking) throws PersistenceException;

    /**
     * This method updates booking in database
     * @param booking Booking object which needs to be updated
     * @throws PersistenceException if database is not available
     */
    void update(Booking booking) throws PersistenceException;
}
