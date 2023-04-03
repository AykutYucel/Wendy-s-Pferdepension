package at.ac.tuwien.sepm.assignment.individual.fleet.service;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.VehicleSearchFilter;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.FileNotOpenedException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.UnsupportedImageException;

import java.util.List;

/**
 * The <code>FleetService</code> is capable to calculate the answer to
 * <blockquote>Ultimate Question of Life, the Universe, and Everything</blockquote>.
 * Depending on the implementation it might take a while.
 */
public interface FleetService {

    /**
     * This method is passing the vehicle from ui and passes it to persistence layer to create a vehicle
     * @param vehicle Vehicle object whill will be used to create
     * @throws UnsupportedImageException if image is not supported
     * @throws FileNotOpenedException if image path is wrong
     * @throws ServiceException if database is not available
     */
    void saveVehicle(Vehicle vehicle) throws UnsupportedImageException, FileNotOpenedException, ServiceException;

    /**
     * This method is used to pass the id from ui to persistence to get desired vehicle
     * @param id Vehicle id that we need to get vehicle object with all values.
     * @return Vehicle object that we search through id
     * @throws ServiceException if database is not available
     */
    Vehicle getVehicle(int id) throws ServiceException;

    /**
     * This method is used to pass Filter object and passes it to persistence to find related rows
     * @param vehicleSearchFilter Filter object that contains filters
     * @return List of vehicle that contains related vehicles
     * @throws ServiceException if database is not available
     */
    List<Vehicle> findVehicle(VehicleSearchFilter vehicleSearchFilter) throws ServiceException;

    /**
     * This method is used to get vehicle object from ui and passes it to persistence to delete a row from database
     * @param vehicle Vehicle object which will be used to delete
     * @throws ServiceException if database is not available
     */
    void deleteVehicle(Vehicle vehicle) throws ServiceException;

    /**
     * This method is used to get vehicle object from ui and passes it to persistence to update database
     * @param vehicle Vehicle object which will be used to update
     * @throws UnsupportedImageException if image is not supported
     * @throws FileNotOpenedException if image path is wrong
     * @throws ServiceException if database is not available
     */
    void editVehicle(Vehicle vehicle) throws UnsupportedImageException, FileNotOpenedException, ServiceException;

}
