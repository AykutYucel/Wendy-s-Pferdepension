package at.ac.tuwien.sepm.assignment.individual.fleet.ui;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.*;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ViewBookingDetailsController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Booking booking;
    private BookingVehicleService bookingVehicleService;

    public ViewBookingDetailsController(Booking booking, BookingVehicleService bookingVehicleService){
        this.booking = booking;
        this.bookingVehicleService = bookingVehicleService;
    }

    @FXML
    private TableView<BookingVehicle> Result_TV;

    @FXML
    private TableColumn<BookingVehicle, String> VehicleList_TC;

    @FXML
    private TableColumn<BookingVehicle, Double> VehiclePriceList_TC;

    @FXML
    private TableColumn<BookingVehicle, String> LicenseNumber_TC;

    @FXML
    private TableColumn<BookingVehicle, Date> LicenseDate_TC;

    @FXML
    private TextField Id_TF;

    @FXML
    private TextField Beginning_TF;

    @FXML
    private TextField Status_TF;

    @FXML
    private TextField Name_TF;

    @FXML
    private TextField TotalPrice_TF;

    @FXML
    private TextField Ending_TF;

    @FXML
    private TextField BillingDate_TF;

    @FXML
    private TextField Payment_TF;

    @FXML
    private TextField DateOfIssue_TF;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("Booking detail window is initialized.");
        setTable();
    }

    public void setTable(){
        ObservableList<BookingVehicle> list = null;
        try {
            list = FXCollections.observableArrayList(bookingVehicleService.findBookingVehicle(booking));
        } catch (ServiceException e) {
            LOG.error("Database connection error.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        VehicleList_TC.setCellValueFactory(new PropertyValueFactory<>("model"));
        VehiclePriceList_TC.setCellValueFactory(new PropertyValueFactory<>("price"));
        LicenseNumber_TC.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        LicenseDate_TC.setCellValueFactory(new PropertyValueFactory<>("licenseDate"));


        Result_TV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        Result_TV.setItems(list);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm");

        Id_TF.setText(booking.getId() + "");
        Beginning_TF.setText(booking.getFromDate().format(dateTimeFormatter));
        Status_TF.setText(booking.getStatus().name());
        Name_TF.setText(booking.getName());
        TotalPrice_TF.setText(booking.getTotalPrice() + "");
        Ending_TF.setText(booking.getToDate().format(dateTimeFormatter));
        BillingDate_TF.setText(booking.getBillingDate() == null ? "" : booking.getBillingDate() + "");
        Payment_TF.setText(booking.getPayment_info());
        DateOfIssue_TF.setText(booking.getDateOfIssue().toLocalDateTime().format(dateTimeFormatter));

    }
}
