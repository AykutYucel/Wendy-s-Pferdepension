package at.ac.tuwien.sepm.assignment.individual.fleet.exception;

public class VehicleNotAvailableInTimePeriodException extends Exception {
    public VehicleNotAvailableInTimePeriodException() { super(); }
    public VehicleNotAvailableInTimePeriodException(String message) { super(message); }
    public VehicleNotAvailableInTimePeriodException(String message, Throwable cause) { super(message, cause); }
    public VehicleNotAvailableInTimePeriodException(Throwable cause) { super(cause); }
}
