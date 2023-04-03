package at.ac.tuwien.sepm.assignment.individual.fleet.vehicle;

import at.ac.tuwien.sepm.assignment.individual.fleet.dao.VehicleDAO;
import at.ac.tuwien.sepm.assignment.individual.fleet.dao.VehicleDAOJDBC;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.PersistenceException;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class VehicleDAONormalTest {
    VehicleDAO vehicleDAO = new VehicleDAOJDBC();

    @Test
    public void AddVehicleValidateTest() throws PersistenceException {

        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        Vehicle vehicle = new Vehicle();

        vehicle.setModel("BMW X1");
        vehicle.setPrice(20);
        vehicle.setConstruction_date(2010);
        vehicle.setDescription("Black");
        vehicle.setSeating(4);
        vehicle.setLicense("A");
        vehicle.setType("Motorized");
        vehicle.setPower(175);
        vehicle.setPicture("");
        vehicle.setCreation_time(timestamp);

        boolean result = false;
        vehicleDAO.create(vehicle);
        result = true;
        assertTrue(result);
    }

    @Test
    public void AddVehicleValidateTest2() throws PersistenceException {

        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        Vehicle vehicle = new Vehicle();

        vehicle.setModel("BMW X2");
        vehicle.setPrice(20);
        vehicle.setConstruction_date(2013);
        vehicle.setDescription("Black");
        vehicle.setSeating(4);
        vehicle.setLicense("A");
        vehicle.setType("Motorized");
        vehicle.setPower(175);
        vehicle.setPicture("file:src/main/resources/images/noImage.jpeg");
        vehicle.setCreation_time(timestamp);

        boolean result = false;
        vehicleDAO.create(vehicle);
        result = true;
        assertTrue(result);
    }

    @Test
    public void AddVehicleValidateTest3() throws PersistenceException {

        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        Vehicle vehicle = new Vehicle();

        vehicle.setModel("BMW X5");
        vehicle.setPrice(20);
        vehicle.setConstruction_date(2013);
        vehicle.setDescription("Black");
        vehicle.setType("Motorized");
        vehicle.setLicense("ABC");
        vehicle.setPower(175);
        vehicle.setPicture("file:src/main/resources/images/sport.jpeg");
        vehicle.setCreation_time(timestamp);

        boolean result = false;
        vehicleDAO.create(vehicle);
        result = true;
        assertTrue(result);
    }
}
