package at.ac.tuwien.sepm.assignment.individual.fleet.exception;

public class CompleteBookingServiceException extends Exception{
    public CompleteBookingServiceException() { super(); }
    public CompleteBookingServiceException(String message) { super(message); }
    public CompleteBookingServiceException(String message, Throwable cause) { super(message, cause); }
    public CompleteBookingServiceException(Throwable cause) { super(cause); }
}
