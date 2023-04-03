package at.ac.tuwien.sepm.assignment.individual.fleet.exception;

public class MandatoryFieldsNotFilledException extends Exception{
    public MandatoryFieldsNotFilledException() { super(); }
    public MandatoryFieldsNotFilledException(String message) { super(message); }
    public MandatoryFieldsNotFilledException(String message, Throwable cause) { super(message, cause); }
    public MandatoryFieldsNotFilledException(Throwable cause) { super(cause); }
}
