package at.ac.tuwien.sepm.assignment.individual.fleet.ui;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.*;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.MandatoryFieldsNotFilledException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.WrongPaymentInformationException;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.BookingService;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.BookingVehicleService;
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
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BookingWindowController implements Initializable{

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Booking booking;
    private List<Vehicle> vehicleList;
    private BookingService bookingService;
    private BookingVehicleService bookingVehicleService;
    List<BookingVehicle> bookingVehicleList;
    private SearchTabController searchTabController;


    BookingWindowController(SearchTabController searchTabController, Booking booking, List<Vehicle> vehicleList, BookingService bookingService, BookingVehicleService bookingVehicleService){
        this.vehicleList = vehicleList;
        this.bookingService = bookingService;
        this.booking = booking;
        this.bookingVehicleService = bookingVehicleService;
        this.searchTabController = searchTabController;
    }

    @FXML
    private ChoiceBox<String> PaymentType_CB;

    @FXML
    private TextField CustomerName_TF;

    @FXML
    private TextField IBANNumber_TF;

    @FXML
    private TextField CreditCardNumber_TF;

    @FXML
    private TextField BeginningDate_TF;

    @FXML
    private TextField EndingDate_TF;

    @FXML
    private TableView<Vehicle> Result_TV;

    @FXML
    private TableColumn<Vehicle, String> Model_TC;

    @FXML
    private TableColumn<Vehicle, Integer> License_TC;

    @FXML
    private TableColumn<Vehicle, Date> Year_TC;

    @FXML
    private TableColumn<Vehicle, Double> Price_TC;

    @FXML
    private Button AddLicenseInfoButton;

    @FXML
    void onAddLicenseButtonClicked(ActionEvent event) {
        LOG.info("Add booking button is clicked.");
        if(Result_TV.getSelectionModel().getSelectedItem().getLicense().equals(LicenseTypes.Non.name())){
            LOG.error("Vehicle does not require a license.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No license information required!");
            alert.setContentText("This vehicle doesn't required license information");
            alert.showAndWait();
        }else{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LicenseInfoWindow.fxml"));
            try {
                LicenseInfoController licenseInfoController= new LicenseInfoController(bookingVehicleList ,Result_TV.getSelectionModel().getSelectedItem());
                fxmlLoader.setControllerFactory(param -> param.isInstance(licenseInfoController) ? licenseInfoController : null);
                Parent root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.initStyle(StageStyle.DECORATED);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.setTitle(Result_TV.getSelectionModel().getSelectedItem().getModel());
                stage.show();
            }catch (IOException e){
                LOG.error("FXML file load failed.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("FXML file loading is failed!");
                alert.setContentText(e.toString());
                alert.showAndWait();
            }
        }
    }

    @FXML
    void onCancelButtonClicked(ActionEvent event) {
        LOG.info("Cancel button in new booking window is clicked.");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm closing!");
        alert.setHeaderText("Are you certain that you want to close before completing this booking?");
        if(alert.showAndWait().get().getButtonData().isDefaultButton()){
            Node source = (Node)  event.getSource();
            Stage current_stage  = (Stage) source.getScene().getWindow();
            current_stage.close();
        }
    }

    @FXML
    void onFinishButtonClicked(ActionEvent event) {
        LOG.info("Finish booking button is clicked.");
        try {
            try {
                checkInputs();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                if(!checkFreeCancellation(alert)){
                    if(alert.showAndWait().get().getButtonData().isDefaultButton()){
                        booking.setName(CustomerName_TF.getText());
                        if(PaymentType_CB.getSelectionModel().getSelectedIndex() == 0){
                            booking.setPayment_type(true);
                            booking.setPayment_info(CreditCardNumber_TF.getText());
                        }else{
                            booking.setPayment_type(false);
                            booking.setPayment_info(IBANNumber_TF.getText());
                        }
                        booking.setStatus(BookingStatus.open);
                    }
                }else{
                    booking.setName(CustomerName_TF.getText());
                    if(PaymentType_CB.getSelectionModel().getSelectedIndex() == 0){
                        booking.setPayment_type(true);
                        booking.setPayment_info(CreditCardNumber_TF.getText());
                    }else{
                        booking.setPayment_type(false);
                        booking.setPayment_info(IBANNumber_TF.getText());
                    }
                    booking.setStatus(BookingStatus.open);
                }
                try {
                    bookingService.bookVehicle(booking, vehicleList);
                    for(BookingVehicle bookingVehicle : bookingVehicleList){
                        bookingVehicle.setBooking_id(booking.getId());
                        bookingVehicleService.bookingVehicle(bookingVehicle);
                    }
                } catch (ServiceException e) {
                    LOG.error("Database connection error.");
                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setTitle("Error");
                    alert2.setHeaderText(e.getMessage());
                    alert2.setContentText(e.toString());
                    alert2.showAndWait();
                }

                searchTabController.setTable();
                Node source = (Node)  event.getSource();
                Stage current_stage  = (Stage) source.getScene().getWindow();
                current_stage.close();
                LOG.info("Booking is successful.");
            } catch (WrongPaymentInformationException e) {
                LOG.error("Payment information is wrong.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Wrong Payment Information!");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }

        }catch (MandatoryFieldsNotFilledException e){
            LOG.error("Mandatory fields are not filled.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Mandatory Fields are not filled!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("Booking window is initialized.");
        fillMenuButtons();
        setTable();
        createBookingVehicle(vehicleList);
        tableViewListener(Result_TV);

        addTextLimiter(CustomerName_TF, 20);
        addTextLimiter(IBANNumber_TF, 20);
        addTextLimiter(CreditCardNumber_TF, 20);
    }

    private void createBookingVehicle(List<Vehicle> vehicleList){
        bookingVehicleList = new ArrayList<>();
        for(Vehicle vehicle : vehicleList){
            BookingVehicle bookingVehicle = new BookingVehicle();
            bookingVehicle.setBooking_id(booking.getId());
            bookingVehicle.setVehicle_id(vehicle.getId());
            bookingVehicle.setPrice(vehicle.getPrice());
            bookingVehicle.setModel(vehicle.getModel());
            bookingVehicleList.add(bookingVehicle);
        }
    }

    private void fillMenuButtons(){
        choiceBoxListener(PaymentType_CB);
        PaymentType_CB.setItems(FXCollections.observableArrayList("Credit Card", "Debit Card"));
        PaymentType_CB.getSelectionModel().selectFirst();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm");
        BeginningDate_TF.setText(booking.getFromDate().format(dateTimeFormatter));
        EndingDate_TF.setText(booking.getToDate().format(dateTimeFormatter));

    }

    private void setTable(){
        ObservableList<Vehicle> list = FXCollections.observableArrayList(vehicleList);
        Model_TC.setCellValueFactory(new PropertyValueFactory<>("model"));
        License_TC.setCellValueFactory(new PropertyValueFactory<>("license"));
        Year_TC.setCellValueFactory(new PropertyValueFactory<>("construction_date"));
        Price_TC.setCellValueFactory(new PropertyValueFactory<>("price"));

        Result_TV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Result_TV.setItems(list);
        AddLicenseInfoButton.setDisable(true);
    }

    private void choiceBoxListener (ChoiceBox choiceBox){
        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection == "Credit Card"){
                CreditCardNumber_TF.setDisable(false);
                IBANNumber_TF.setDisable(true);
            }else{
                CreditCardNumber_TF.setDisable(true);
                IBANNumber_TF.setDisable(false);
            }
        });
    }

    private void tableViewListener(TableView tableView){
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null){
                AddLicenseInfoButton.setDisable(false);
            }
        });
    }

    private void checkInputs() throws MandatoryFieldsNotFilledException {
        if(CustomerName_TF.getText().isEmpty()){
            throw new MandatoryFieldsNotFilledException("Customer name is missing!");
        }else if(PaymentType_CB.getSelectionModel().getSelectedIndex() == 0 && CreditCardNumber_TF.getText().isEmpty()){
            throw new MandatoryFieldsNotFilledException("Credit Card number is missing!");
        }else if(PaymentType_CB.getSelectionModel().getSelectedIndex() == 1 && IBANNumber_TF.getText().isEmpty()){
            throw new MandatoryFieldsNotFilledException("IBAN number is missing!");
        }

        for (Vehicle vehicle : vehicleList) {
            if(!vehicle.getLicense().equals(LicenseTypes.Non.name())){
                for (BookingVehicle bookingVehicle : bookingVehicleList) {
                    if(bookingVehicle.getVehicle_id() == vehicle.getId()){
                        if(bookingVehicle.getLicenseNumber() == null || bookingVehicle.getLicenseDate() == null){
                            throw new MandatoryFieldsNotFilledException("License informations are not specified!");
                        }
                    }
                }
            }
        }
    }

    private boolean checkFreeCancellation(Alert alert){
        if(java.time.LocalDateTime.now().until(booking.getFromDate(), ChronoUnit.HOURS) < 168){
            alert.setTitle("Confirm Booking");
            alert.setHeaderText("Free cancellation period is over! Do you still want to book the vehicle(s)?");
            return false;
        }
        return true;
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