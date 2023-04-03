package at.ac.tuwien.sepm.assignment.individual.fleet.exception;

public class CancelingBookingServiceException extends Exception{
    public CancelingBookingServiceException() { super(); }
    public CancelingBookingServiceException(String message) { super(message); }
    public CancelingBookingServiceException(String message, Throwable cause) { super(message, cause); }
    public CancelingBookingServiceException(Throwable cause) { super(cause); }
}
