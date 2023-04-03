package at.ac.tuwien.sepm.assignment.individual.fleet.service;

import at.ac.tuwien.sepm.assignment.individual.fleet.dao.VehicleDAO;
import at.ac.tuwien.sepm.assignment.individual.fleet.dao.VehicleDAOJDBC;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.VehicleSearchFilter;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.FileNotOpenedException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.UnsupportedImageException;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.Calendar;
import java.util.List;


public class SimpleFleetService implements FleetService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private VehicleDAO vehicleDAO = new VehicleDAOJDBC();

    private java.sql.Timestamp getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        return new java.sql.Timestamp(now.getTime());
    }

    @Override
    public void saveVehicle(Vehicle vehicle) throws UnsupportedImageException, FileNotOpenedException, ServiceException {
        LOG.info("Fleet service save vehicle.");
        File file;
        Image image;
        String name;
        if(!vehicle.getPicture().isEmpty()){
            try {
                file = new File(vehicle.getPicture());
                image = new Image(new FileInputStream(vehicle.getPicture()));
            } catch (FileNotFoundException e) {
                LOG.debug("Throw no such file.");
                throw new FileNotOpenedException("There is no such file!");
            }

            if(file.length() > 625000 || image.getWidth() < 500 || image.getHeight() < 500){
                LOG.debug("Throw unsupported image.");
                throw new UnsupportedImageException("Unsupported image selected!");
            }else{
                name = file.getName();
                InputStream is;
                OutputStream os;

                try {
                    is = new FileInputStream(vehicle.getPicture());
                    os = new FileOutputStream("src/main/resources/images/" + name); // relative path / ile baslamaz!
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                    is.close();
                    os.close();
                    vehicle.setPicture("file:src/main/resources/images/" + name);
                } catch (IOException e) {
                    LOG.debug("Throw no such file.");
                    throw new FileNotOpenedException("There is no such file!");
                }
            }
        }else{
            vehicle.setPicture("file:src/main/resources/images/noImage.jpeg");
        }
        vehicle.setCreation_time(getCurrentTime());
        try {
            vehicleDAO.create(vehicle);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }

    }

    @Override
    public Vehicle getVehicle(int id) throws ServiceException{
        LOG.info("Fleet service get vehicle.");
        try {
            return vehicleDAO.get(id);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<Vehicle> findVehicle(VehicleSearchFilter vehicleSearchFilter) throws ServiceException{
        LOG.info("Fleet service find vehicle.");
        try {
            return vehicleDAO.find(vehicleSearchFilter);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void deleteVehicle(Vehicle vehicle) throws ServiceException{
        LOG.info("Fleet service delete vehicle.");
        vehicle.setUpdate_time(getCurrentTime());
        try {
            vehicleDAO.delete(vehicle);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void editVehicle(Vehicle vehicle) throws UnsupportedImageException, FileNotOpenedException, ServiceException{
        LOG.info("Fleet service edit vehicle.");
        File file;
        Image image;
        String name;
        boolean check;
        if(!vehicle.getPicture().isEmpty()){
            try {
                file = new File(vehicle.getPicture());
                image = new Image(new FileInputStream(vehicle.getPicture()));
                check = new File("src/main/resources/images/", file.getName()).exists();
            } catch (FileNotFoundException e) {
                LOG.debug("Throw no such file.");
                throw new FileNotOpenedException("Wrong image path");
            }

            if(file.length() > 625000 || image.getWidth() < 500 || image.getHeight() < 500){
                LOG.debug("Throw unsupported image.");
                throw new UnsupportedImageException("Unsupported Image");
            }else if(!check){
                name = file.getName();
                InputStream is;
                OutputStream os;

                try {
                    is = new FileInputStream(vehicle.getPicture());
                    os = new FileOutputStream("src/main/resources/images/" + name); // relative path / ile baslamaz!
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                    is.close();
                    os.close();
                    vehicle.setPicture("file:src/main/resources/images/" + name);
                } catch (IOException e) {
                    LOG.debug("Throw no such file.");
                    throw new FileNotOpenedException("Wrong image path");
                }
            }else {
                name = file.getName();
                vehicle.setPicture("file:src/main/resources/images/" + name);
            }
        }else{
            vehicle.setPicture("file:src/main/resources/images/noImage.jpeg");
        }
        vehicle.setUpdate_time(getCurrentTime());
        try {
            vehicleDAO.edit(vehicle);
        } catch (PersistenceException e) {
            LOG.debug("Throw service exception.");
            throw new ServiceException(e.getMessage());
        }
    }

}
