package at.ac.tuwien.sepm.assignment.individual.fleet.service;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.*;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.*;

import java.util.List;

public interface BookingService {
    /**
     * This methods gets booking information from ui layer and pass is to persistent layer
     * @param booking Booking object that will be used for booking
     * @param vehicleList List that contains all vehicles in related booking.
     * @throws WrongPaymentInformationException if payment validation failed
     * @throws ServiceException if database is not available
     */
    void bookVehicle(Booking booking, List<Vehicle> vehicleList) throws WrongPaymentInformationException, ServiceException;

    /**
     * This methods gets booking information from ui layer and pass is to persistent layer
     * @param booking Booking object that will be used for booking
     * @param vehicle Vehicle will be added if payment validation is successful
     * @param vehicleSearchFilter Filter object will be used to determine time period
     * @throws WrongPaymentInformationException if payment validation failed
     * @throws VehicleNotAvailableInTimePeriodException if vehicle is not available during time
     * @throws ServiceException if database is not available
     */
    void bookVehicle(Booking booking, Vehicle vehicle, VehicleSearchFilter vehicleSearchFilter) throws WrongPaymentInformationException, VehicleNotAvailableInTimePeriodException, ServiceException;

    /**
     * This method gets Filter object and returns List with associated booking
     * @param bookingSearchFilter Filter object will be used to determine time period and status
     * @return List of associated bookings
     * @throws ServiceException if database is not available
     */
    List<Booking> findBooking(BookingSearchFilter bookingSearchFilter) throws ServiceException;

    /**
     *  This method gets Filter object and returns List with associated booking
     * @param chartFilter Filter object will be used to determine time period for chart
     * @return List of associated bookings
     * @throws ServiceException if database is not available
     */
    List<ChartInformation> findBooking(ChartFilter chartFilter) throws ServiceException;

    /**
     *  This method gets booking and pass it to persistence to change its status to completed
     * @param booking Booking object that will be used for update
     * @throws CompleteBookingServiceException if Complete not possible during time
     * @throws ServiceException if database is not available
     */
    void completeBooking(Booking booking) throws CompleteBookingServiceException, ServiceException;

    /**
     *
     * @param booking Booking object that will be used for update
     * @param feeDecision Boolean that contains decision of user.
     * @throws CancelingBookingServiceException if booking is not possible during time
     * @throws CancellationFeeException if free cancellation is over
     * @throws ServiceException if database is not available
     */
    void cancelBooking(Booking booking, boolean feeDecision) throws CancelingBookingServiceException, CancellationFeeException, ServiceException;

    /**
     * This method pass the information to edit a booking and its relation with vehicle table from ui to persistence
     * @param booking Booking object that will be used for update
     * @param vehicleList List of vehicle will be used to create a relation between tables
     * @throws WrongPaymentInformationException if payment validation failed
     * @throws ServiceException if database is not available
     */
    void editBooking(Booking booking, List<Vehicle> vehicleList) throws WrongPaymentInformationException, ServiceException;
}
