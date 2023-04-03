package at.ac.tuwien.sepm.assignment.individual.fleet.ui;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.BookingVehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.MandatoryFieldsNotFilledException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

public class LicenseInfoController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private List<BookingVehicle> bookingVehicleList;
    private Vehicle vehicle;

    public LicenseInfoController(List<BookingVehicle> bookingVehicleList, Vehicle vehicle){
        this.bookingVehicleList = bookingVehicleList;
        this.vehicle = vehicle;
    }

    public LicenseInfoController(BookingVehicle bookingVehicle, Vehicle vehicle){
        this.bookingVehicleList = new ArrayList<>();
        this.vehicle = vehicle;
        bookingVehicleList.add(bookingVehicle);
    }

    @FXML
    private TextField LicenseInfoLicenseNumber_TF;

    @FXML
    private DatePicker LicenseInfoLicenseDate_DP;

    @FXML
    private RadioButton LicenseA;

    @FXML
    private RadioButton LicenseB;

    @FXML
    private RadioButton LicenseC;

    @FXML
    void onAddButtonClicked(ActionEvent event) {
        LOG.info("License add button is clicked.");
        try {
            checkInputs();
            if(LicenseA.isSelected() || LicenseC.isSelected()){
                if(getCurrentTime().toLocalDateTime().until(LicenseInfoLicenseDate_DP.getValue().atStartOfDay(), ChronoUnit.YEARS) > -3){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid driving license!");
                    alert.setContentText("License must be minimum 3 years old!");
                    alert.showAndWait();
                }else{
                    for (BookingVehicle bookingVehicle : bookingVehicleList) {
                        if(bookingVehicle.getVehicle_id() == vehicle.getId()){
                            bookingVehicle.setLicenseNumber(LicenseInfoLicenseNumber_TF.getText());
                            bookingVehicle.setLicenseDate(java.sql.Date.valueOf(LicenseInfoLicenseDate_DP.getValue()));
                            Node source = (Node)  event.getSource();
                            Stage current_stage  = (Stage) source.getScene().getWindow();
                            current_stage.close();
                        }
                    }
                }
            }else{
                for (BookingVehicle bookingVehicle : bookingVehicleList) {
                    if(bookingVehicle.getVehicle_id() == vehicle.getId()){
                        bookingVehicle.setLicenseNumber(LicenseInfoLicenseNumber_TF.getText());
                        bookingVehicle.setLicenseDate(java.sql.Date.valueOf(LicenseInfoLicenseDate_DP.getValue()));
                        Node source = (Node)  event.getSource();
                        Stage current_stage  = (Stage) source.getScene().getWindow();
                        current_stage.close();
                    }
                }
            }
        } catch (MandatoryFieldsNotFilledException e) {
            LOG.error("Mandatory fields are not filled.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Mandatory fields are not filled!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private java.sql.Timestamp getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        return new java.sql.Timestamp(now.getTime());
    }

    private void checkInputs() throws MandatoryFieldsNotFilledException {
        if(LicenseInfoLicenseNumber_TF.getText().isEmpty()){
            throw new MandatoryFieldsNotFilledException("License Number is missing!");
        }else if(LicenseInfoLicenseDate_DP.getValue() == null){
            throw new MandatoryFieldsNotFilledException("License Date is missing!");
        }else if(!LicenseA.isSelected() && !LicenseB.isSelected() && !LicenseC.isSelected()){
            throw new MandatoryFieldsNotFilledException("License type is not selected!");
        }else if(getCurrentTime().toLocalDateTime().until(LicenseInfoLicenseDate_DP.getValue().atStartOfDay(), ChronoUnit.MINUTES) > 0){
            throw new MandatoryFieldsNotFilledException("Wrong license date!");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("License info window is initialized.");
        if(vehicle.getLicense().contains("A")){
            LicenseA.setDisable(false);
        }if(vehicle.getLicense().contains("B")){
            LicenseB.setDisable(false);
        }if(vehicle.getLicense().contains("C")){
            LicenseC.setDisable(false);
        }
        addTextLimiter(LicenseInfoLicenseNumber_TF, 20);
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
