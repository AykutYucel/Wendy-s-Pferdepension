package at.ac.tuwien.sepm.assignment.individual.fleet.dao;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.*;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.VehicleNotAvailableInTimePeriodException;
import at.ac.tuwien.sepm.assignment.individual.fleet.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOJDBC implements BookingDAO{

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private String bookingInsertString = "INSERT INTO BOOKING (ID, NAME, PAYMENT_INFO, FROM_DATETIME, TO_DATETIME, TOTAL_PRICE, STATUS, DATE_OF_ISSUE) VALUES (default, ?, ?, ?, ?, ?, ?, ?)";
    private String bookingCompleteString = "UPDATE BOOKING SET STATUS = ?, BILLING_DATE = ? WHERE ID = ?";
    private String bookingUpdateString = "UPDATE BOOKING SET STATUS = ?, BILLING_DATE = ?, TOTAL_PRICE = ? WHERE ID = ?";
    private String bookingEditString = "UPDATE BOOKING SET NAME = ?, PAYMENT_INFO = ?, TOTAL_PRICE = ? WHERE ID = ?";
    private String bookingDeleteString = "DELETE FROM BOOKING WHERE ID = ?";
    private String lineChartString = "select BOOKING_PRICE, NAME from booking_vehicle join booking on (booking_vehicle.booking_id = booking.id) join vehicle on (booking_vehicle.vehicle_id = vehicle.id)";


    @Override
    public void book(Booking booking) throws PersistenceException{
        LOG.info("BookingDAOJDBC book.");
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement(bookingInsertString, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, booking.getName());
            ps.setString(2, booking.getPayment_info());
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(booking.getFromDate()));
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(booking.getToDate()));
            ps.setDouble(5, booking.getTotalPrice());
            ps.setString(6, booking.getStatus().name());
            ps.setTimestamp(7, booking.getDateOfIssue());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            generatedKeys.next();
            booking.setId(generatedKeys.getInt(1));
        } catch (SQLException e) {
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }


    }

    @Override
    public void book(Booking booking, VehicleSearchFilter vehicleSearchFilter, Vehicle vehicle) throws VehicleNotAvailableInTimePeriodException, PersistenceException {
        LOG.info("BookingDAOJDBC book.");
        try {
            System.out.println(vehicleAvailableCheckFilter(vehicleSearchFilter, vehicle));
            PreparedStatement ps1 = DBUtil.getConnection().prepareStatement(vehicleAvailableCheckFilter(vehicleSearchFilter, vehicle));
            ResultSet rs = ps1.executeQuery();
            if(!rs.next()){
                LOG.debug("Throw vehicle not available.");
                throw new VehicleNotAvailableInTimePeriodException("This vehicle is not available during this period!");
            }else{
                PreparedStatement ps = DBUtil.getConnection().prepareStatement(bookingInsertString, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, booking.getName());
                ps.setString(2, booking.getPayment_info());
                ps.setTimestamp(3, java.sql.Timestamp.valueOf(booking.getFromDate()));
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(booking.getToDate()));
                ps.setDouble(5, booking.getTotalPrice());
                ps.setString(6, booking.getStatus().name());
                ps.setTimestamp(7, booking.getDateOfIssue());
                ps.executeUpdate();
                ResultSet generatedKeys = ps.getGeneratedKeys();
                generatedKeys.next();
                booking.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }
    }

    @Override
    public List<Booking> find(BookingSearchFilter bookingSearchFilter) throws PersistenceException{
        LOG.info("BookingDAOJDBC find.");
        List<Booking> list = new ArrayList<>();

        try{
            PreparedStatement ps = DBUtil.getConnection().prepareStatement("select * from booking " + getFullFilter(bookingSearchFilter)+ " ORDER BY FROM_DATETIME DESC ");
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Booking booking = new Booking();
                booking.setId(rs.getInt("ID"));
                booking.setName(rs.getString("NAME"));
                booking.setPayment_info(rs.getString("PAYMENT_INFO"));
                booking.setFromDate(rs.getTimestamp("FROM_DATETIME").toLocalDateTime());
                booking.setToDate(rs.getTimestamp("TO_DATETIME").toLocalDateTime());
                booking.setTotalPrice(rs.getDouble("TOTAL_PRICE"));
                booking.setStatus(BookingStatus.valueOf(rs.getString("STATUS")));
                booking.setDateOfIssue(rs.getTimestamp("DATE_OF_ISSUE"));
                booking.setBillingDate(rs.getTimestamp("BILLING_DATE"));

                list.add(booking);
            }
        }catch (SQLException e){
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }
        return list;
    }

    @Override
    public List<ChartInformation> find(ChartFilter chartFilter) throws PersistenceException{
        LOG.info("BookingDAOJDBC find.");
        List<ChartInformation> list = new ArrayList<>();
        try{
            PreparedStatement ps = DBUtil.getConnection().prepareStatement(lineChartString + getFullLineChartFilter(chartFilter));
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                ChartInformation chartInformation = new ChartInformation();
                chartInformation.setLocalDateTime(chartFilter.getDate().toLocalDateTime());
                chartInformation.setPrice(rs.getDouble("BOOKING_PRICE"));
                chartInformation.setCustomerName("NAME");

                list.add(chartInformation);
            }
        }catch (SQLException e){
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }
        return list;
    }

    @Override
    public void complete(Booking booking) throws PersistenceException{
        LOG.info("BookingDAOJDBC complete.");
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement(bookingCompleteString);
            ps.setString(1, "completed");
            ps.setTimestamp(2, booking.getBillingDate());
            ps.setInt(3, booking.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }
    }

    @Override
    public void delete(Booking booking) throws PersistenceException{
        LOG.info("BookingDAOJDBC delete.");
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement(bookingDeleteString);
            ps.setInt(1, booking.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }
    }

    @Override
    public void cancel(Booking booking) throws PersistenceException{
        LOG.info("BookingDAOJDBC cancel.");
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement(bookingUpdateString);
            ps.setString(1, "canceled");
            ps.setTimestamp(2, booking.getBillingDate());
            ps.setDouble(3,booking.getTotalPrice());
            ps.setInt(4, booking.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }
    }

    @Override
    public void update(Booking booking) throws PersistenceException{
        LOG.info("BookingDAOJDBC booking.");
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement(bookingEditString);
            ps.setString(1, booking.getName());
            ps.setString(2, booking.getPayment_info());
            ps.setDouble(3,booking.getTotalPrice());
            ps.setInt(4, booking.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.debug("Throw persistence exception.");
            throw new PersistenceException("Connection is not established");
        }
    }

    private String getFullFilter(BookingSearchFilter bookingSearchFilter){
        ArrayList<String> bookingFilter = new ArrayList<>();
        String bookingFilterString = "";

        if(bookingSearchFilter.isStatusFilter()){
            if(bookingSearchFilter.getStatus().equals("Booking")){
                bookingFilter.add("status = '" + BookingStatus.open.name() + "'");
            }else{
                bookingFilter.add("status = '" + BookingStatus.canceled.name() + "' or status = '" + BookingStatus.completed.name() + "'");
            }
        }

        if(bookingFilter.size()>0){
            bookingFilterString += " where ";
            bookingFilterString += String.join(" AND ", bookingFilter);
        }
        return bookingFilterString;
    }

    private String getFullLineChartFilter(ChartFilter lineChartFilter){
        ArrayList<String> chartFilter = new ArrayList<>();
        String chartFilterString = "";

        if(lineChartFilter.isLicenseAfilter()){
            chartFilter.add("license LIKE '%A%'");
        }else if(lineChartFilter.isLicenseBfilter()){
            chartFilter.add("license LIKE '%B%'");
        }else if(lineChartFilter.isLicenseCfilter()){
            chartFilter.add("license LIKE '%C%'");
        } else if(lineChartFilter.isLicenseNonfilter()){
            chartFilter.add("license LIKE 'Non%'");
        }

        if(lineChartFilter.isDateFilter()){
            chartFilter.add("FROM_DATETIME <= '" + lineChartFilter.getDate() + "' and TO_DATETIME > '" + lineChartFilter.getDate() + "'");
        }

        if((chartFilter.size()>0)){
            chartFilterString += " where ";
            chartFilterString += String.join(" AND ", chartFilter);
        }
        return chartFilterString;

    }

    private String vehicleAvailableCheckFilter(VehicleSearchFilter vehicleSearchFilter, Vehicle vehicle){
        String a = "select id from vehicle WHERE ISACTIVE = TRUE"+ " and vehicle.id = " + vehicle.getId() + " and vehicle.id not in (SELECT booking_vehicle.vehicle_id FROM BOOKING_VEHICLE join vehicle on (vehicle.id = vehicle_id) join booking on (booking.id = booking_id) where status = 'open'";
        if(vehicleSearchFilter.isFromDateFilter() && vehicleSearchFilter.isToDateFilter()){
            a += " and ((from_datetime < " + "'" + Timestamp.valueOf(vehicleSearchFilter.getToDate()) + "'" + " and from_datetime >= " + "'" + Timestamp.valueOf(vehicleSearchFilter.getFromDate()) + "')";
            a += " or (to_datetime <= " + "'" + Timestamp.valueOf(vehicleSearchFilter.getToDate()) + "'" + " and to_datetime > " + "'" + Timestamp.valueOf(vehicleSearchFilter.getFromDate()) + "')";
            a += " or (from_datetime >= " + "'" + Timestamp.valueOf(vehicleSearchFilter.getFromDate()) + "'" + " and to_datetime <= " + "'" + Timestamp.valueOf(vehicleSearchFilter.getToDate()) + "')";
            a += " or (from_datetime <= " + "'" + Timestamp.valueOf(vehicleSearchFilter.getFromDate()) + "'" + " and to_datetime >= " + "'" + Timestamp.valueOf(vehicleSearchFilter.getToDate()) + "')))";


        }
        return a;
    }
}
