package at.ac.tuwien.sepm.assignment.individual.fleet.exception;

public class FileNotOpenedException extends Exception {
    public FileNotOpenedException() { super(); }
    public FileNotOpenedException(String message) { super(message); }
    public FileNotOpenedException(String message, Throwable cause) { super(message, cause); }
    public FileNotOpenedException(Throwable cause) { super(cause); }
}
