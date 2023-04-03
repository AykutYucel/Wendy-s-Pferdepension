package at.ac.tuwien.sepm.assignment.individual.fleet.exception;

public class WrongPaymentInformationException extends Exception{
    public WrongPaymentInformationException() { super(); }
    public WrongPaymentInformationException(String message) { super(message); }
    public WrongPaymentInformationException(String message, Throwable cause) { super(message, cause); }
    public WrongPaymentInformationException(Throwable cause) { super(cause); }
}
