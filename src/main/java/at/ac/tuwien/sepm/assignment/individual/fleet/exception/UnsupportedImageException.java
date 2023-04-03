package at.ac.tuwien.sepm.assignment.individual.fleet.exception;

public class UnsupportedImageException extends Exception{
    public UnsupportedImageException() { super(); }
    public UnsupportedImageException(String message) { super(message); }
    public UnsupportedImageException(String message, Throwable cause) { super(message, cause); }
    public UnsupportedImageException(Throwable cause) { super(cause); }
}
