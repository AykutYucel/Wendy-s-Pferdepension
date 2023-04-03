package at.ac.tuwien.sepm.assignment.individual.fleet.dao;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Booking;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.BookingVehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingVehicleDAOJDBC implements BookingVehicleDAO {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private String bookingVehicleInsertString = "INSERT INTO BOOKING_VEHICLE (BOOKING_ID, VEHICLE_ID, BOOKING_PRICE, LICENSE_NUMBER, LICENSE_DATE, MODEL) VALUES (?, ?, ?, ?, ?, ?)";
    private String bookingVehicleDeleteString = "DELETE FROM BOOKING_VEHICLE WHERE BOOKING_ID = ?";
    private String bookingVehicleDeleteString2 = "DELETE FROM BOOKING_VEHICLE WHERE BOOKING_ID = ? AND VEHICLE_ID = ?";

    @Override
    public void bookingVehicle(BookingVehicle bookingVehicle) throws PersistenceException{
        LOG.info("BookingVehicleDAOJDBC booking vehicle.");
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement(bookingVehicleInsertString);
            ps.setInt(1, bookingVehicle.getBooking_id());
            ps.setInt(2, bookingVehicle.getVehicle_id());
            ps.setDouble(3, bookingVehicle.getPrice());
            ps.setString(4, bookingVehicle.getLicenseNumber());
            ps.setDate(5, bookingVehicle.getLicenseDate());
            ps.setString(6, bookingVehicle.getModel());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }
    }

    @Override
    public List<BookingVehicle> find(Booking booking) throws PersistenceException{
        LOG.info("BookingVehicleDAOJDBC find.");
        List<BookingVehicle> list = new ArrayList<>();
        try{
            PreparedStatement ps = DBUtil.getConnection().prepareStatement("SELECT * FROM booking_vehicle WHERE BOOKING_ID = ?");
            ps.setInt(1, booking.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                BookingVehicle bookingVehicle = new BookingVehicle();
                bookingVehicle.setBooking_id(rs.getInt("BOOKING_ID"));
                bookingVehicle.setVehicle_id(rs.getInt("VEHICLE_ID"));
                bookingVehicle.setLicenseDate(rs.getDate("LICENSE_DATE"));
                bookingVehicle.setLicenseNumber(rs.getString("LICENSE_NUMBER"));
                bookingVehicle.setPrice(rs.getDouble("BOOKING_PRICE"));
                bookingVehicle.setModel(rs.getString("MODEL"));

                list.add(bookingVehicle);
            }
        }catch (SQLException e){
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }

        return list;
    }

    @Override
    public void delete(Booking booking) throws PersistenceException{
        LOG.info("BookingVehicleDAOJDBC delete.");
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement(bookingVehicleDeleteString);
            ps.setInt(1, booking.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }
    }

    @Override
    public BookingVehicle getBookingVehicle(int bookingId, int vehicleId) throws PersistenceException{
        LOG.info("BookingVehicleDAOJDBC get booking vehicle.");
        BookingVehicle bookingVehicle = null;
        try{
            bookingVehicle = new BookingVehicle();
            PreparedStatement ps = DBUtil.getConnection().prepareStatement("SELECT * FROM booking_vehicle WHERE BOOKING_ID = ? AND VEHICLE_ID = ?");
            ps.setInt(1, bookingId);
            ps.setInt(2, vehicleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){

                bookingVehicle.setBooking_id(rs.getInt("BOOKING_ID"));
                bookingVehicle.setVehicle_id(rs.getInt("VEHICLE_ID"));
                bookingVehicle.setLicenseDate(rs.getDate("LICENSE_DATE"));
                bookingVehicle.setLicenseNumber(rs.getString("LICENSE_NUMBER"));
                bookingVehicle.setPrice(rs.getDouble("BOOKING_PRICE"));
                bookingVehicle.setModel(rs.getString("MODEL"));

            }

        }catch (SQLException e){
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }
        return bookingVehicle;
    }

    @Override
    public void delete(BookingVehicle bookingVehicle) throws PersistenceException{
        LOG.info("BookingVehicleDAOJDBC delete.");
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement(bookingVehicleDeleteString2);
            ps.setInt(1, bookingVehicle.getBooking_id());
            ps.setInt(2, bookingVehicle.getVehicle_id());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }
    }
}
