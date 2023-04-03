package at.ac.tuwien.sepm.assignment.individual.fleet.ui;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.*;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.MandatoryFieldsNotFilledException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.VehicleNotAvailableInTimePeriodException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.WrongPaymentInformationException;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.BookingService;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.BookingVehicleService;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.SimpleBookingService;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.SimpleBookingVehicleService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BookingInDetailsController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Vehicle vehicle;
    private Stage parentStage;
    private Booking booking;
    private BookingService bookingService;
    private BookingVehicleService bookingVehicleService;
    List<BookingVehicle> bookingVehicleList;
    List<Vehicle> vehicleList;
    BookingVehicle bookingVehicle;


    public BookingInDetailsController(Vehicle vehicle, Stage stage){
        this.vehicle = vehicle;
        this.parentStage = stage;
        booking = new Booking();
        bookingService = new SimpleBookingService();
        bookingVehicleService = new SimpleBookingVehicleService();
        bookingVehicleList = new ArrayList<>();
        vehicleList = new ArrayList<>();
        vehicleList.add(vehicle);
        bookingVehicle = new BookingVehicle();
        bookingVehicle.setVehicle_id(vehicle.getId());
        bookingVehicle.setModel(vehicle.getModel());
        bookingVehicle.setPrice(vehicle.getPrice());
        bookingVehicleList.add(bookingVehicle);
    }



    @FXML
    private TextField CustomerName_TF;

    @FXML
    private TextField CreditCard_TF;

    @FXML
    private TextField IBANNumber_TF;

    @FXML
    private ChoiceBox PaymentType_CB;

    @FXML
    private ChoiceBox BeginningHour_CB;

    @FXML
    private ChoiceBox EndingHour_CB;

    @FXML
    private DatePicker BeginningDate_DP;

    @FXML
    private DatePicker EndingDate_DP;

    @FXML
    private Button addLicenseButton;

    @FXML
    void onAddLicenseButtonClicked(ActionEvent event) {
        LOG.info("Add license button is clicked.");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LicenseInfoWindow.fxml"));
        try {
            LicenseInfoController licenseInfoController= new LicenseInfoController(bookingVehicleList , vehicle);
            fxmlLoader.setControllerFactory(param -> param.isInstance(licenseInfoController) ? licenseInfoController : null);
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle(vehicle.getModel());
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

    @FXML
    void onCancelButtonClicked(ActionEvent event) {
        LOG.info("Cancel button is clicked.");
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onFinishButtonClicked(ActionEvent event) {
        LOG.info("Booking finish button is clicked.");
        try {
            try {
                checkInputs();
                LocalTime begTime = LocalTime.of(Integer.parseInt(BeginningHour_CB.getValue().toString().substring(0,2)), Integer.parseInt(BeginningHour_CB.getValue().toString().substring(3,5)));
                LocalTime endTime = LocalTime.of(Integer.parseInt(EndingHour_CB.getValue().toString().substring(0,2)), Integer.parseInt(EndingHour_CB.getValue().toString().substring(3,5)));
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                booking.setFromDate(createDateTime(BeginningDate_DP.getValue(), begTime));
                booking.setToDate(createDateTime(EndingDate_DP.getValue(), endTime));
                if(!checkFreeCancellation(alert)){
                    if(alert.showAndWait().get().getButtonData().isDefaultButton()){
                        booking.setName(CustomerName_TF.getText());
                        if(PaymentType_CB.getSelectionModel().getSelectedIndex() == 0){
                            booking.setPayment_type(true);
                            booking.setPayment_info(CreditCard_TF.getText());
                        }else{
                            booking.setPayment_type(false);
                            booking.setPayment_info(IBANNumber_TF.getText());
                        }
                        booking.setStatus(BookingStatus.open);
                    }else{
                        return;
                    }
                }else{
                    booking.setName(CustomerName_TF.getText());
                    if(PaymentType_CB.getSelectionModel().getSelectedIndex() == 0){
                        booking.setPayment_type(true);
                        booking.setPayment_info(CreditCard_TF.getText());
                    }else{
                        booking.setPayment_type(false);
                        booking.setPayment_info(IBANNumber_TF.getText());
                    }
                    booking.setStatus(BookingStatus.open);
                }
                VehicleSearchFilter vehicleSearchFilter = new VehicleSearchFilter();
                vehicleSearchFilter.setFromDate(createDateTime(BeginningDate_DP.getValue(), begTime));
                vehicleSearchFilter.setToDate(createDateTime(EndingDate_DP.getValue(), endTime));
                try {
                    bookingService.bookVehicle(booking, vehicle, vehicleSearchFilter);
                    bookingVehicleList.get(0).setBooking_id(booking.getId());
                    bookingVehicleService.bookingVehicle(bookingVehicleList.get(0));
                } catch (ServiceException e) {
                    LOG.error("Database connection error.");
                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setTitle("Error");
                    alert2.setHeaderText(e.getMessage());
                    alert2.setContentText(e.toString());
                    alert2.showAndWait();
                }
                Node source = (Node)  event.getSource();
                Stage current_stage  = (Stage) source.getScene().getWindow();
                current_stage.close();
                LOG.info("Booking adding is successful.");
            } catch (WrongPaymentInformationException e) {
                LOG.error("Payment information is wrong.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Wrong Payment Information!");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            } catch (VehicleNotAvailableInTimePeriodException e) {
                LOG.error("Vehicle is not available in time period.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Booking is not successful!");
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
        LOG.info("Booking in detail window is initialized.");
        choiceBoxListener(PaymentType_CB);
        fillMenu();
        if(!vehicle.getLicense().equals(LicenseTypes.Non.name())){
            addLicenseButton.setDisable(false);
        }else{
            addLicenseButton.setDisable(true);
        }
        addTextLimiter(CustomerName_TF, 20);
        addTextLimiter(CreditCard_TF, 20);
        addTextLimiter(IBANNumber_TF, 20);
    }

    private void choiceBoxListener (ChoiceBox choiceBox){
        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection == "Credit Card"){
                CreditCard_TF.setDisable(false);
                IBANNumber_TF.setDisable(true);
            }else{
                CreditCard_TF.setDisable(true);
                IBANNumber_TF.setDisable(false);
            }
        });
    }

    private void checkInputs() throws MandatoryFieldsNotFilledException {
        LocalTime begTime = LocalTime.of(Integer.parseInt(BeginningHour_CB.getValue().toString().substring(0,2)), Integer.parseInt(BeginningHour_CB.getValue().toString().substring(3,5)));
        LocalTime endTime = LocalTime.of(Integer.parseInt(EndingHour_CB.getValue().toString().substring(0,2)), Integer.parseInt(EndingHour_CB.getValue().toString().substring(3,5)));

        LocalDateTime begDateTime;
        LocalDateTime endDateTime;
        if (CustomerName_TF.getText().isEmpty()) {
            throw new MandatoryFieldsNotFilledException("Customer name is missing!");
        } else if (PaymentType_CB.getSelectionModel().getSelectedIndex() == 0 && CreditCard_TF.getText().isEmpty()) {
            throw new MandatoryFieldsNotFilledException("Credit Card number is missing!");
        } else if (PaymentType_CB.getSelectionModel().getSelectedIndex() == 1 && IBANNumber_TF.getText().isEmpty()) {
            throw new MandatoryFieldsNotFilledException("IBAN number is missing!");
        } else if(BeginningDate_DP.getValue() == null || EndingDate_DP.getValue() == null){
            throw new MandatoryFieldsNotFilledException("Booking period is not specified!");
        }
        begDateTime = createDateTime(BeginningDate_DP.getValue(), begTime);
        endDateTime = createDateTime(EndingDate_DP.getValue(), endTime);

        if(endDateTime.isBefore(begDateTime) || endDateTime.equals(begDateTime)){
            throw new MandatoryFieldsNotFilledException("Beginning date can not be equal or later than ending date!");
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

    private void fillMenu(){
        BeginningHour_CB.setItems(FXCollections.observableArrayList("00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"));
        EndingHour_CB.setItems(FXCollections.observableArrayList("00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"));

        BeginningHour_CB.getSelectionModel().selectFirst();
        EndingHour_CB.getSelectionModel().selectFirst();

        PaymentType_CB.setItems(FXCollections.observableArrayList("Credit Card", "Debit Card"));
        PaymentType_CB.getSelectionModel().selectFirst();
    }

    private boolean checkFreeCancellation(Alert alert){
        if(java.time.LocalDateTime.now().until(booking.getFromDate(), ChronoUnit.HOURS) < 168){
            alert.setTitle("Confirm Booking");
            alert.setHeaderText("Free cancellation period is over! Do you still want to book the vehicle(s)?");
            return false;
        }
        return true;
    }

    private LocalDateTime createDateTime(LocalDate localDate, LocalTime localTime){
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return localDateTime;
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
