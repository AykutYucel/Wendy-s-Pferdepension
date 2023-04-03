package at.ac.tuwien.sepm.assignment.individual.fleet.exception;

public class CancellationFeeException extends Exception{
    public CancellationFeeException() { super(); }
    public CancellationFeeException(String message) { super(message); }
    public CancellationFeeException(String message, Throwable cause) { super(message, cause); }
    public CancellationFeeException(Throwable cause) { super(cause); }
}
