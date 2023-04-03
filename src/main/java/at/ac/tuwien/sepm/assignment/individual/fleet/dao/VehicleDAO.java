package at.ac.tuwien.sepm.assignment.individual.fleet.dao;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.VehicleSearchFilter;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.PersistenceException;

import java.util.List;

public interface VehicleDAO {

    /**
     * This method creates new Vehicle in database
     * @param vehicle Vehicle object which will be used to create.
     * @throws PersistenceException if database is not available
     */
    void create(Vehicle vehicle) throws PersistenceException;

    /**
     * This method returns the vehicle by id
     * @param id Integer that will be used to find vehicle
     * @return Related vehicle
     * @throws PersistenceException if database is not available
     */
    Vehicle get(int id) throws PersistenceException;

    /**
     * This method returns all vehicles in our search criterion
     * @param vehicleSearchFilter Filter object that is used to create query
     * @return List that contains all related vehicles
     * @throws PersistenceException if database is not available
     */
    List<Vehicle> find(VehicleSearchFilter vehicleSearchFilter) throws PersistenceException;

    /**
     * This method deletes a vehicle in database.
     * @param vehicle Vehicle object that is used for deletion
     * @throws PersistenceException if database is not available
     */
    void delete(Vehicle vehicle) throws PersistenceException;

    /**
     * This methods updates a vehicle in database
     * @param vehicle Vehicle object that is used for update.
     * @throws PersistenceException if database is not available
     */
    void edit(Vehicle vehicle) throws PersistenceException;

}
