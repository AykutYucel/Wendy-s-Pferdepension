package at.ac.tuwien.sepm.assignment.individual.fleet.service;


import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Booking;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.BookingVehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;

import java.util.List;

public interface BookingVehicleService {

    /**
     * This method pass the object to create the relation table
     * @param bookingVehicle object will be used to create relation
     * @throws ServiceException if database is not available
     */
    void bookingVehicle(BookingVehicle bookingVehicle) throws ServiceException;

    /**
     * This method pass the information to get the associated relation table
     * @param booking booking object will be used to get necessary tables
     * @return List of booking vehicle that contains related information
     * @throws ServiceException if database is not available
     */
    List<BookingVehicle> findBookingVehicle(Booking booking)throws ServiceException;

    /**
     * This method passes the info to delete booking vehicle
     * @param booking booking object will be used to find rows to be deleted
     * @throws ServiceException if database is not available
     */
    void deleteBookingVehicle(Booking booking)throws ServiceException;

    /**
     * This method passes the info to delete booking vehicle
     * @param bookingVehicle bookingvehicle object will be used to find rows to be deleted
     * @throws ServiceException if database is not available
     */
    void deleteBookingVehicle(BookingVehicle bookingVehicle)throws ServiceException;

    /**
     * This method gets the informations to pass to persistence to get the related booking vehicle
     * @param bookingId id of the booking
     * @param vehicleId id of the vehicle
     * @return related booking vehicle object
     * @throws ServiceException if database is not available
     */
    BookingVehicle getBookingVehicle(int bookingId, int vehicleId)throws ServiceException;
}
