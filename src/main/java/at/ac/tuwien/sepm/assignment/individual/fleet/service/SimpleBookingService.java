package at.ac.tuwien.sepm.assignment.individual.fleet.service;

import at.ac.tuwien.sepm.assignment.individual.fleet.dao.BookingDAO;
import at.ac.tuwien.sepm.assignment.individual.fleet.dao.BookingDAOJDBC;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.*;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.*;
import org.apache.commons.validator.routines.CreditCardValidator;
import org.apache.commons.validator.routines.IBANValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class SimpleBookingService implements BookingService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private BookingDAO bookingDAO = new BookingDAOJDBC();
    private IBANValidator ibanValidator = new IBANValidator();
    private CreditCardValidator creditCardValidator = new CreditCardValidator();



    @Override
    public void editBooking(Booking booking, List<Vehicle> vehicleList) throws WrongPaymentInformationException, ServiceException{
        LOG.info("Booking service edit booking.");
        if(!creditCardValidator.isValid(booking.getPayment_info())){
            if(!ibanValidator.isValid(booking.getPayment_info())){
                LOG.debug("Throw wrong payment information.");
                throw new WrongPaymentInformationException("Wrong Payment Information!");
            }
        }
        booking.setTotalPrice(calculateTotalPrice(vehicleList, booking));
        try {
            bookingDAO.update(booking);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void bookVehicle(Booking booking, List<Vehicle> vehicleList) throws WrongPaymentInformationException, ServiceException {
        LOG.info("Booking service book vehicle.");
        paymentValidate(booking);
        booking.setDateOfIssue(getCurrentTime());
        booking.setTotalPrice(calculateTotalPrice(vehicleList, booking));
        try {
            bookingDAO.book(booking);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void bookVehicle(Booking booking, Vehicle vehicle, VehicleSearchFilter vehicleSearchFilter) throws WrongPaymentInformationException, VehicleNotAvailableInTimePeriodException , ServiceException{
        LOG.info("Booking service book vehicle.");
        paymentValidate(booking);
        booking.setDateOfIssue(getCurrentTime());
        booking.setTotalPrice(vehicle.getPrice());
        try {
            bookingDAO.book(booking, vehicleSearchFilter, vehicle);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<Booking> findBooking(BookingSearchFilter bookingSearchFilter) throws ServiceException{
        LOG.info("Booking service find booking.");
        try {
            return bookingDAO.find(bookingSearchFilter);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<ChartInformation> findBooking(ChartFilter chartFilter) throws ServiceException{
        LOG.info("Booking service find booking.");
        try {
            return bookingDAO.find(chartFilter);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void completeBooking(Booking booking) throws CompleteBookingServiceException, ServiceException{
        LOG.info("Booking service complete booking.");
        if(booking.getStatus().equals(BookingStatus.open)){
            if(java.time.LocalDateTime.now().until(booking.getFromDate(), ChronoUnit.SECONDS) <= 0){
                booking.setBillingDate(getCurrentTime());
                try {
                    bookingDAO.complete(booking);
                } catch (PersistenceException e) {
                    LOG.debug("Throw service exception.");
                    throw new ServiceException(e.getMessage());
                }
            }else{
                LOG.debug("Throw complete booking exception.");
                throw new CompleteBookingServiceException("Booking completing is only possible after beginning of booking date!");
            }
        }else{
            LOG.debug("Throw complete booking exception.");
            throw new CompleteBookingServiceException("Invoice is already completed");
        }
    }

    @Override
    public void cancelBooking(Booking booking, boolean feeDecision) throws CancelingBookingServiceException, CancellationFeeException, ServiceException{
        LOG.info("Booking service cancel booking.");
        BookingVehicleService bookingVehicleService = new SimpleBookingVehicleService();
        if(checkCancellationFee(booking) == 0){
            bookingVehicleService.deleteBookingVehicle(booking);
            try {
                bookingDAO.delete(booking);
            } catch (PersistenceException e) {
                LOG.debug("Throw service exception.");
                throw new ServiceException(e.getMessage());
            }
        }else{
            if(!feeDecision){
                if(checkCancellationFee(booking) == 1){
                    LOG.debug("Throw free cancellation period over.");
                    throw new CancellationFeeException("Free cancellation period is over! Do you accept to pay %40 of total price to cancel?");
                }else if(checkCancellationFee(booking) == 2){
                    LOG.debug("Throw free cancellation period over.");
                    throw new CancellationFeeException("Free cancellation period is over! Do you accept to pay %75 of total price to cancel?");
                }else{
                    LOG.debug("Throw free cancellation period over.");
                    throw new CancelingBookingServiceException("Booking cancellation period is over!");
                }
            }else{
                booking.setBillingDate(getCurrentTime());
                try {
                    bookingDAO.cancel(booking);
                } catch (PersistenceException e) {
                    LOG.debug("Throw service exception.");
                    throw new ServiceException(e.getMessage());
                }
            }
        }
    }

    private double calculateTotalPrice(List<Vehicle> vehicleList, Booking booking){
        double totalPrice = 0;
        double temp;
        Iterator iterator = vehicleList.iterator();
        while (iterator.hasNext()){
            Vehicle vehicle = (Vehicle) iterator.next();
            totalPrice += vehicle.getPrice();
        }
        long hours = (booking.getFromDate().until(booking.getToDate(), ChronoUnit.HOURS));
        temp = totalPrice * hours;
        return temp;
    }

    private void paymentValidate(Booking booking) throws WrongPaymentInformationException {
        if(booking.isPayment_type()){
            if(!creditCardValidator.isValid(booking.getPayment_info())){
                LOG.debug("Throw wrong payment info.");
                throw new WrongPaymentInformationException("Wrong Credit Card number!");
            }
        }else{
            if(!ibanValidator.isValid(booking.getPayment_info())){
                LOG.debug("Throw wrong payment info.");
                throw new WrongPaymentInformationException("Wrong IBAN number!");
            }
        }
    }

    private int checkCancellationFee(Booking booking) {
        if(java.time.LocalDateTime.now().until(booking.getFromDate(), ChronoUnit.HOURS) >= 168){
            return 0;
        }else if(java.time.LocalDateTime.now().until(booking.getFromDate(), ChronoUnit.HOURS) >= 72){
            return 1;
        }else if(java.time.LocalDateTime.now().until(booking.getFromDate(), ChronoUnit.HOURS) >= 24){
            return 2;
        }else{
            return 3;
        }
    }

    private java.sql.Timestamp getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        return new java.sql.Timestamp(now.getTime());
    }

}
