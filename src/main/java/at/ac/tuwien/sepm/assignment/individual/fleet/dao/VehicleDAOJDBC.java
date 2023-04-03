package at.ac.tuwien.sepm.assignment.individual.fleet.dao;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.VehicleSearchFilter;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAOJDBC implements VehicleDAO {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private String vehicleInsertString = "INSERT INTO VEHICLE (ID, MODEL, CONST_DATE, DESCRIPTION, SEATING, LICENSE, PLATE, TYPE, POWER, PRICE, CREATE_DATE, PICTURE, UPDATE_DATE) VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private String vehicleSelectString = "SELECT * FROM VEHICLE WHERE ID = ?";
    private String vehicleDeleteString = "UPDATE VEHICLE SET ISACTIVE = ?, UPDATE_DATE = ? WHERE ID = ?";
    private String vehicleUpdateString = "UPDATE VEHICLE SET MODEL = ?, CONST_DATE = ?, DESCRIPTION = ?, SEATING = ?, LICENSE = ?, PLATE = ?, TYPE = ?, POWER = ?, PRICE = ?, PICTURE = ?, UPDATE_DATE = ? WHERE ID = ?";


    @Override
    public void create(Vehicle vehicle) throws PersistenceException{
        LOG.info("VehicleDAOJDBC create.");
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement(vehicleInsertString, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, vehicle.getModel());
            ps.setInt(2, vehicle.getConstruction_date());
            ps.setString(3, vehicle.getDescription());
            ps.setInt(4, vehicle.getSeating());
            ps.setString(5, vehicle.getLicense());
            ps.setString(6, vehicle.getPlate());
            ps.setString(7, vehicle.getType());
            ps.setDouble(8, vehicle.getPower());
            ps.setDouble(9, vehicle.getPrice());
            ps.setTimestamp(10, vehicle.getCreation_time());
            ps.setString(11, vehicle.getPicture());
            ps.setTimestamp(12, vehicle.getUpdate_time());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            generatedKeys.next();
            vehicle.setId(generatedKeys.getInt(1));
        } catch (SQLException e) {
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }

    }

    @Override
    public Vehicle get(int id) throws PersistenceException{
        LOG.info("VehicleDAOJDBC get.");
        Vehicle vehicle = new Vehicle();

        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement(vehicleSelectString);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                vehicle.setId(id);
                vehicle.setModel(rs.getString("MODEL"));
                vehicle.setConstruction_date(rs.getInt("CONST_DATE"));
                vehicle.setDescription(rs.getString("DESCRIPTION"));
                vehicle.setSeating(rs.getInt("SEATING"));
                vehicle.setLicense(rs.getString("LICENSE"));
                vehicle.setPlate(rs.getString("PLATE"));
                vehicle.setType(rs.getString("TYPE"));
                vehicle.setPower(rs.getDouble("POWER"));
                vehicle.setPrice(rs.getDouble("PRICE"));
                vehicle.setCreation_time(rs.getTimestamp("CREATE_DATE"));
                vehicle.setPicture(rs.getString("PICTURE"));
                vehicle.setUpdate_time(rs.getTimestamp("UPDATE_DATE"));
                vehicle.setActive(rs.getBoolean("ISACTIVE"));
            }
        } catch (SQLException e) {
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }

        return vehicle;

    }

    @Override
    public List<Vehicle> find(VehicleSearchFilter vehicleSearchFilter) throws PersistenceException{
        LOG.info("VehicleDAOJDBC find.");
        List<Vehicle> list = new ArrayList<>();
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement("select * from vehicle WHERE ISACTIVE = TRUE" + getFullFilter(vehicleSearchFilter));
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Vehicle vehicle = new Vehicle();
                vehicle.setId(rs.getInt("ID"));
                vehicle.setModel(rs.getString("MODEL"));
                vehicle.setConstruction_date(rs.getInt("CONST_DATE"));
                vehicle.setDescription(rs.getString("DESCRIPTION"));
                vehicle.setSeating(rs.getInt("SEATING"));
                vehicle.setLicense(rs.getString("LICENSE"));
                vehicle.setPlate(rs.getString("PLATE"));
                vehicle.setType(rs.getString("TYPE"));
                vehicle.setPower(rs.getDouble("POWER"));
                vehicle.setPrice(rs.getDouble("PRICE"));
                vehicle.setCreation_time(rs.getTimestamp("CREATE_DATE"));
                vehicle.setPicture(rs.getString("PICTURE"));
                vehicle.setUpdate_time(rs.getTimestamp("UPDATE_DATE"));
                vehicle.setActive(rs.getBoolean("ISACTIVE"));

                list.add(vehicle);
            }
        } catch (SQLException e) {
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }

        return list;
    }

    @Override
    public void delete(Vehicle vehicle) throws PersistenceException{
        LOG.info("VehicleDAOJDBC delete.");
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement(vehicleDeleteString);
            ps.setBoolean(1, false);
            ps.setTimestamp(2,vehicle.getUpdate_time());
            ps.setInt(3,vehicle.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }
    }

    @Override
    public void edit(Vehicle vehicle) throws PersistenceException{
        LOG.info("VehicleDAOJDBC edit.");
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement(vehicleUpdateString);
            ps.setString(1, vehicle.getModel());
            ps.setInt(2, vehicle.getConstruction_date());
            ps.setString(3, vehicle.getDescription());
            ps.setInt(4, vehicle.getSeating());
            ps.setString(5, vehicle.getLicense());
            ps.setString(6, vehicle.getPlate());
            ps.setString(7, vehicle.getType());
            ps.setDouble(8, vehicle.getPower());
            ps.setDouble(9, vehicle.getPrice());
            ps.setString(10, vehicle.getPicture());
            ps.setTimestamp(11, vehicle.getUpdate_time());
            ps.setInt(12, vehicle.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }
    }

    public String getFullFilter(VehicleSearchFilter vehicleSearchFilter) {
        String vehicleFilterString = "";
        ArrayList<String> vehicleFilter = new ArrayList<>();

        if(vehicleSearchFilter.isSeatingFilter()){
            vehicleFilter.add("seating = " + vehicleSearchFilter.getSeating());
        }

        if(vehicleSearchFilter.isModelFilter()){
            vehicleFilter.add("UPPER(model) LIKE UPPER('%" + vehicleSearchFilter.getModel() + "%')");
        }

        if(vehicleSearchFilter.isEngineFilter()){
            vehicleFilter.add("type = ?");
            PreparedStatement p = null;
            //p.setString(1, vehicleSearchFilter.getEngine());
        }

        if(vehicleSearchFilter.isPriceFilter()){
            if(vehicleSearchFilter.getMinPrice() > 0){
                vehicleFilter.add("price >= " + vehicleSearchFilter.getMinPrice());
            }
            if(vehicleSearchFilter.getMaxPrice() > 0){
                vehicleFilter.add("price <= " + vehicleSearchFilter.getMaxPrice());
            }
        }

        if(vehicleSearchFilter.isLicenseFilter()){
            if(vehicleSearchFilter.getLicense().equals("Non")){
                vehicleFilter.add("license LIKE '" + vehicleSearchFilter.getLicense() + "'");
            }else{
                vehicleFilter.add("license LIKE '%" + vehicleSearchFilter.getLicense() + "%'");
            }
        }

        if(vehicleSearchFilter.isFromDateFilter() || vehicleSearchFilter.isToDateFilter()){
            String a = "vehicle.id not in (SELECT booking_vehicle.vehicle_id FROM BOOKING_VEHICLE join vehicle on (vehicle.id = vehicle_id) join booking on (booking.id = booking_id) where status = 'open' and (";
            if(vehicleSearchFilter.isFromDateFilter() && vehicleSearchFilter.isToDateFilter()){
                a += "(from_datetime < " + "'" + Timestamp.valueOf(vehicleSearchFilter.getToDate()) + "'" + " and from_datetime >= " + "'" + Timestamp.valueOf(vehicleSearchFilter.getFromDate()) + "')";
                a += "or (to_datetime <= " + "'" + Timestamp.valueOf(vehicleSearchFilter.getToDate()) + "'" + " and to_datetime > " + "'" + Timestamp.valueOf(vehicleSearchFilter.getFromDate()) + "')";
                a += "or (from_datetime >= " + "'" + Timestamp.valueOf(vehicleSearchFilter.getFromDate()) + "'" + " and to_datetime <= " + "'" + Timestamp.valueOf(vehicleSearchFilter.getToDate()) + "')";
                a += "or (from_datetime <= " + "'" + Timestamp.valueOf(vehicleSearchFilter.getFromDate()) + "'" + " and to_datetime >= " + "'" + Timestamp.valueOf(vehicleSearchFilter.getToDate()) + "')";


            }else if(vehicleSearchFilter.isFromDateFilter()){
                a += "from_datetime <= " + "'" + Timestamp.valueOf(vehicleSearchFilter.getFromDate()) + "'" + " and to_datetime > " + "'" + Timestamp.valueOf(vehicleSearchFilter.getFromDate()) + "'";
            }else{
                a += "to_datetime >= " + "'" + Timestamp.valueOf(vehicleSearchFilter.getToDate()) + "'" + " and from_datetime < " + "'" + Timestamp.valueOf(vehicleSearchFilter.getToDate()) + "'";
            }
            a += "))";
            vehicleFilter.add(a);
        }


        if(vehicleFilter.size()>0){
            vehicleFilterString += " AND ";
            vehicleFilterString += String.join(" AND ", vehicleFilter);
        }
        return vehicleFilterString;

    }
}
