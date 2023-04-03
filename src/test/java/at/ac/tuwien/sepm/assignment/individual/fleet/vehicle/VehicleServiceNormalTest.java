package at.ac.tuwien.sepm.assignment.individual.fleet.vehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.FileNotOpenedException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.UnsupportedImageException;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.FleetService;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.SimpleFleetService;
import org.junit.Test;

import static org.junit.Assert.*;

public class VehicleServiceNormalTest {

    FleetService fleetService = new SimpleFleetService();

    @Test
    public void AddVehicleValidateTest() throws UnsupportedImageException, FileNotOpenedException, ServiceException {

        Vehicle vehicle = new Vehicle();

        vehicle.setModel("BMW");
        vehicle.setPrice(30);
        vehicle.setConstruction_date(2014);
        vehicle.setDescription("Black");
        vehicle.setSeating(4);
        vehicle.setLicense("A");
        vehicle.setType("Motorized");
        vehicle.setPower(120);
        vehicle.setPicture("");

        boolean result = false;
        fleetService.saveVehicle(vehicle);
        result = true;
        assertTrue(result);
    }

    @Test
    public void AddVehicleValidateTest2() throws UnsupportedImageException, FileNotOpenedException, ServiceException {

        Vehicle vehicle = new Vehicle();

        vehicle.setModel("Hundai");
        vehicle.setPrice(10);
        vehicle.setConstruction_date(2004);
        vehicle.setDescription("White");
        vehicle.setSeating(4);
        vehicle.setLicense("A");
        vehicle.setType("Motorized");
        vehicle.setPower(120);
        vehicle.setPicture("/Users/chriswanted/Downloads/PicturesforSEPM/sport.jpeg");

        boolean result = false;
        fleetService.saveVehicle(vehicle);
        result = true;
        assertTrue(result);
    }

    @Test
    public void AddVehicleValidateTest3() throws UnsupportedImageException, FileNotOpenedException, ServiceException {

        Vehicle vehicle = new Vehicle();

        vehicle.setModel("Bisan");
        vehicle.setPrice(10);
        vehicle.setConstruction_date(2008);
        vehicle.setDescription("White");
        vehicle.setSeating(1);
        vehicle.setLicense("Non");
        vehicle.setType("Brawn");
        vehicle.setPicture("");

        boolean result = false;
        fleetService.saveVehicle(vehicle);
        result = true;
        assertTrue(result);
    }
}
