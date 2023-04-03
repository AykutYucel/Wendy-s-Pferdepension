package at.ac.tuwien.sepm.assignment.individual.fleet.ui;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.*;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.MandatoryFieldsNotFilledException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.WrongPaymentInformationException;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.BookingService;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.BookingVehicleService;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.FleetService;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.SimpleFleetService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EditBookingWindowController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Booking booking;
    private BookingVehicleService bookingVehicleService;
    private FleetService fleetService;
    private VehicleSearchFilter vehicleSearchFilter;
    private List<BookingVehicle> removedList;
    private List<Vehicle> addedList;
    private BookingService bookingService;
    private BookingAndInvoiceTabController bookingAndInvoiceTabController;
    private ObservableList<BookingVehicle> oldList;
    private ObservableList<Vehicle> newList;
    private List<Vehicle> updateList;

    public EditBookingWindowController(BookingAndInvoiceTabController bookingAndInvoiceTabController, Booking booking, BookingVehicleService bookingVehicleService, BookingService bookingService){
        this.bookingAndInvoiceTabController = bookingAndInvoiceTabController;
        this.booking = booking;
        this.bookingVehicleService = bookingVehicleService;
        this.fleetService = new SimpleFleetService();
        this.vehicleSearchFilter = new VehicleSearchFilter();
        this.removedList = new ArrayList<>();
        this.addedList = new ArrayList<>();
        this.bookingService = bookingService;
        this.updateList = new ArrayList<>();
    }

    @FXML
    private TableView<BookingVehicle> OldResult_TV;

    @FXML
    private TableColumn<BookingVehicle, String> OldVehicleList_TC;

    @FXML
    private TableColumn<BookingVehicle, Double> OldVehiclePriceList_TC;

    @FXML
    private TextField Beginning_TF;

    @FXML
    private TextField Name_TF;

    @FXML
    private TextField Ending_TF;

    @FXML
    private TableView<Vehicle> NewResult_TV;

    @FXML
    private TableColumn<Vehicle, String> NewVehicleList_TC;

    @FXML
    private TableColumn<Vehicle, String> NewLicenseList_TC;

    @FXML
    private TableColumn<Vehicle, String> NewEngineList_TC;

    @FXML
    private TableColumn<Vehicle, ?> NewVehiclePriceList_TC;

    @FXML
    private TextField Payment_TF;

    @FXML
    private Button AddButton;

    @FXML
    private Button RemoveButton;

    @FXML
    void onAddButtonClicked(ActionEvent event) {
        LOG.info("Add new vehicle to booking button is clicked.");
        BookingVehicle bookingVehicle = new BookingVehicle();
        bookingVehicle.setPrice(NewResult_TV.getSelectionModel().getSelectedItem().getPrice());
        bookingVehicle.setModel(NewResult_TV.getSelectionModel().getSelectedItem().getModel());
        bookingVehicle.setBooking_id(booking.getId());
        bookingVehicle.setVehicle_id(NewResult_TV.getSelectionModel().getSelectedItem().getId());
        if(!NewResult_TV.getSelectionModel().getSelectedItem().getLicense().equals(LicenseTypes.Non.name())){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LicenseInfoWindow.fxml"));
            try {
                LicenseInfoController licenseInfoController= new LicenseInfoController(bookingVehicle, NewResult_TV.getSelectionModel().getSelectedItem());
                fxmlLoader.setControllerFactory(param -> param.isInstance(licenseInfoController) ? licenseInfoController : null);
                Parent root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.initStyle(StageStyle.DECORATED);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.setTitle(NewResult_TV.getSelectionModel().getSelectedItem().getModel());
                stage.showAndWait();

                if(bookingVehicle.getLicenseNumber() != null){
                    addedList.add(NewResult_TV.getSelectionModel().getSelectedItem());
                    try {
                        if(removedList.contains(bookingVehicleService.getBookingVehicle(booking.getId(), NewResult_TV.getSelectionModel().getSelectedItem().getId()))){
                            removedList.remove(bookingVehicleService.getBookingVehicle(booking.getId(), NewResult_TV.getSelectionModel().getSelectedItem().getId()));
                        }
                    } catch (ServiceException e) {
                        LOG.error("Database connection error.");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(e.getMessage());
                        alert.setContentText(e.toString());
                        alert.showAndWait();
                    }
                    NewResult_TV.getItems().remove(NewResult_TV.getSelectionModel().getSelectedItem());

                    oldList.add(bookingVehicle);
                }
            }catch (IOException e){
                LOG.error("FXML file load failed.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("FXML file loading is failed!");
                alert.setContentText(e.toString());
                alert.showAndWait();
            }
        }else{
            addedList.add(NewResult_TV.getSelectionModel().getSelectedItem());
            try {
                if(removedList.contains(bookingVehicleService.getBookingVehicle(booking.getId(), NewResult_TV.getSelectionModel().getSelectedItem().getId()))){
                    removedList.remove(bookingVehicleService.getBookingVehicle(booking.getId(), NewResult_TV.getSelectionModel().getSelectedItem().getId()));
                }
            } catch (ServiceException e) {
                LOG.error("Database connection error.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(e.getMessage());
                alert.setContentText(e.toString());
                alert.showAndWait();
            }
            NewResult_TV.getItems().remove(NewResult_TV.getSelectionModel().getSelectedItem());

            oldList.add(bookingVehicle);
        }
        LOG.info("License add to vehicle is successful.");
    }

    @FXML
    void onRemoveButtonClicked(ActionEvent event) {
        LOG.info("Vehicle remove from a booking button is clicked.");
        if(OldResult_TV.getItems().size() == 1){
            LOG.error("Vehicle remove from booking is unsuccessful.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Removing vehicle is failed!");
            alert.setContentText("Removing all vehicles in a particular booking is not possible!");
            alert.showAndWait();
        }else{
            Vehicle vehicle = null;
            try {
                vehicle = fleetService.getVehicle(OldResult_TV.getSelectionModel().getSelectedItem().getVehicle_id());
            } catch (ServiceException e) {
                LOG.error("Database connection error.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(e.getMessage());
                alert.setContentText(e.toString());
                alert.showAndWait();
            }
            newList.add(vehicle);

            removedList.add(OldResult_TV.getSelectionModel().getSelectedItem());
            try {
                if(addedList.contains(fleetService.getVehicle(OldResult_TV.getSelectionModel().getSelectedItem().getVehicle_id()))){
                    addedList.remove(OldResult_TV.getSelectionModel().getSelectedItem().getVehicle_id());
                }
            } catch (ServiceException e) {
                LOG.error("Database connection error.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(e.getMessage());
                alert.setContentText(e.toString());
                alert.showAndWait();
            }
            OldResult_TV.getItems().remove(OldResult_TV.getSelectionModel().getSelectedItem());
            LOG.info("Vehicle remove from booking is successful.");
        }
    }

    @FXML
    void onCancelButtonClicked(ActionEvent event) {
        LOG.info("Edit booking cancel button is clicked.");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm closing!");
        alert.setHeaderText("Are you certain that you want to close before saving this booking?");
        if(alert.showAndWait().get().getButtonData().isDefaultButton()){
            Node source = (Node)  event.getSource();
            Stage current_stage  = (Stage) source.getScene().getWindow();
            current_stage.close();
        }
    }

    @FXML
    void onEditButtonClicked(ActionEvent event) {
        LOG.info("Edit booking button is clicked.");
        try {
            for (BookingVehicle bookingVehicle : oldList){
                try {
                    updateList.add(fleetService.getVehicle(bookingVehicle.getVehicle_id()));
                } catch (ServiceException e) {
                    LOG.error("Database connection error.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(e.getMessage());
                    alert.setContentText(e.toString());
                    alert.showAndWait();
                }
            }
            checkInputs();
            booking.setName(Name_TF.getText());
            booking.setPayment_info(Payment_TF.getText());
            try {
                bookingService.editBooking(booking, updateList);
            } catch (ServiceException e) {
                LOG.error("Database connection error.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(e.getMessage());
                alert.setContentText(e.toString());
                alert.showAndWait();
            }

            for (BookingVehicle bookingVehicle : removedList){
                try {
                    bookingVehicleService.deleteBookingVehicle(bookingVehicle);
                } catch (ServiceException e) {
                    LOG.error("Database connection error.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(e.getMessage());
                    alert.setContentText(e.toString());
                    alert.showAndWait();
                }
            }

            for(BookingVehicle bookingVehicle : oldList){
                try {
                    if(bookingVehicleService.getBookingVehicle(bookingVehicle.getBooking_id(), bookingVehicle.getVehicle_id()).getVehicle_id() != bookingVehicle.getVehicle_id()){
                        bookingVehicleService.bookingVehicle(bookingVehicle);
                    }
                } catch (ServiceException e) {
                    LOG.error("Database connection error.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(e.getMessage());
                    alert.setContentText(e.toString());
                    alert.showAndWait();
                }
            }

            bookingAndInvoiceTabController.setTable();
            Node source = (Node)  event.getSource();
            Stage current_stage  = (Stage) source.getScene().getWindow();
            current_stage.close();
            LOG.info("Edit booking is successful.");

        }catch (MandatoryFieldsNotFilledException e){
            LOG.error("Mandatory fields are not filled.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Mandatory fields are not filled!");
            alert.setContentText(e.getMessage());
        }catch (WrongPaymentInformationException e){
            LOG.error("Payment information is wrong.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Wrong Payment Information!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

    }

    public void setTable(){
        try {
            oldList = FXCollections.observableArrayList(bookingVehicleService.findBookingVehicle(booking));
        } catch (ServiceException e) {
            LOG.error("Database connection error.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        OldVehicleList_TC.setCellValueFactory(new PropertyValueFactory<>("model"));
        OldVehiclePriceList_TC.setCellValueFactory(new PropertyValueFactory<>("price"));

        OldResult_TV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        OldResult_TV.setItems(oldList);
        OldResult_TV.setEditable(true);

        vehicleSearchFilter.setFromDate(booking.getFromDate());
        vehicleSearchFilter.setToDate(booking.getToDate());

        try {
            newList = FXCollections.observableArrayList(fleetService.findVehicle(vehicleSearchFilter));
        } catch (ServiceException e) {
            LOG.error("Database connection error.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.setContentText(e.toString());
            alert.showAndWait();
        }

        NewVehicleList_TC.setCellValueFactory(new PropertyValueFactory<>("model"));
        NewLicenseList_TC.setCellValueFactory(new PropertyValueFactory<>("license"));
        NewEngineList_TC.setCellValueFactory(new PropertyValueFactory<>("type"));
        NewVehiclePriceList_TC.setCellValueFactory(new PropertyValueFactory<>("price"));

        NewResult_TV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        NewResult_TV.setItems(newList);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm");

        Beginning_TF.setText(booking.getFromDate().format(dateTimeFormatter));
        Ending_TF.setText(booking.getToDate().format(dateTimeFormatter));
        Name_TF.setText(booking.getName());
        Payment_TF.setText(booking.getPayment_info());
        RemoveButton.setDisable(true);
        AddButton.setDisable(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("Edit booking window is initialized.");
        setTable();
        oldTableViewListener(OldResult_TV);
        newTableViewListener(NewResult_TV);
        addTextLimiter(Name_TF, 20);
        addTextLimiter(Payment_TF, 20);
    }

    private void oldTableViewListener(TableView tableView){
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null){
                RemoveButton.setDisable(false);
            }else {
                RemoveButton.setDisable(true);
            }
        });
    }

    private void newTableViewListener(TableView tableView){
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null){
                AddButton.setDisable(false);
            }else{
                AddButton.setDisable(true);
            }
        });
    }

    private void checkInputs() throws MandatoryFieldsNotFilledException {
        if(Name_TF.getText().isEmpty()){
            throw new MandatoryFieldsNotFilledException("Customer name is missing!");
        }else if(Payment_TF.getText().isEmpty()){
            throw new MandatoryFieldsNotFilledException("Payment information is missing!");
        }
    }

    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().length() > maxLength) {
                    String s = tf.getText().substring(0, maxLength);
                    tf.setText(s);
                }
            }
        });
    }
}
