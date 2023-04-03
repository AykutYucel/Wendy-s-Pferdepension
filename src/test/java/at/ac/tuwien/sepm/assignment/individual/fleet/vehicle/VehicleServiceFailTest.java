package at.ac.tuwien.sepm.assignment.individual.fleet.vehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.FileNotOpenedException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.UnsupportedImageException;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.FleetService;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.SimpleFleetService;
import org.junit.Test;

public class VehicleServiceFailTest {

    FleetService fleetService = new SimpleFleetService();

    @Test (expected = UnsupportedImageException.class)
    public void AddVehicleValidateTest() throws UnsupportedImageException, FileNotOpenedException, ServiceException {

        Vehicle vehicle = new Vehicle();

        vehicle.setModel("Mercedes");
        vehicle.setPrice(25);
        vehicle.setConstruction_date(2012);
        vehicle.setDescription("Black");
        vehicle.setSeating(4);
        vehicle.setLicense("A");
        vehicle.setType("Motorized");
        vehicle.setPower(120);
        vehicle.setPicture("/Users/chriswanted/Downloads/PicturesforSEPM/smallpixel.jpg");

        fleetService.saveVehicle(vehicle);
    }

    @Test (expected = FileNotOpenedException.class)
    public void AddVehicleValidateTest2() throws UnsupportedImageException, FileNotOpenedException, ServiceException{

        Vehicle vehicle = new Vehicle();

        vehicle.setModel("BMW X5");
        vehicle.setPrice(25);
        vehicle.setConstruction_date(2012);
        vehicle.setDescription("Black");
        vehicle.setSeating(4);
        vehicle.setLicense("A");
        vehicle.setType("Motorized");
        vehicle.setPower(120);
        vehicle.setPicture("1234567890");

        fleetService.saveVehicle(vehicle);
    }

    @Test (expected = FileNotOpenedException.class)
    public void AddVehicleValidateTest3() throws UnsupportedImageException, FileNotOpenedException, ServiceException{

        Vehicle vehicle = new Vehicle();

        vehicle.setModel("Lada Samara");
        vehicle.setPrice(15);
        vehicle.setConstruction_date(2000);
        vehicle.setDescription("Black");
        vehicle.setSeating(4);
        vehicle.setLicense("AB");
        vehicle.setType("Motorized");
        vehicle.setPower(120);
        vehicle.setPicture("SmallPixel.jpeg");

        fleetService.saveVehicle(vehicle);
    }
}
