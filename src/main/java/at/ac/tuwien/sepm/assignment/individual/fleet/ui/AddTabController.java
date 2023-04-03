package at.ac.tuwien.sepm.assignment.individual.fleet.ui;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.EngineTypes;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.FileNotOpenedException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.MandatoryFieldsNotFilledException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.UnsupportedImageException;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.FleetService;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class AddTabController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FleetService fleetService;
    private final Stage stage;


    public AddTabController(FleetService fleetService, Stage stage) {
        this.fleetService = fleetService;
        this.stage = stage;
    }

    @FXML
    private TextField Plate_TF;

    @FXML
    private TextField Model_TF;

    @FXML
    private TextField Description_TF;

    @FXML
    private TextField SeatingCapacity_TF;

    @FXML
    private TextField EnginePower_TF;

    @FXML
    private TextField Price_TF;

    @FXML
    private RadioButton Motorized_RB;

    @FXML
    private RadioButton Brawn_RB;

    @FXML
    private RadioButton LicenseA_RB;

    @FXML
    private RadioButton LicenseB_RB;

    @FXML
    private RadioButton LicenseC_RB;

    @FXML
    private TextField ConstructionYear_TF;

    @FXML
    private TextField Photo_TF;

    @FXML
    void OnAddButtonClicked(ActionEvent event) {
        LOG.info("Vehicle add button is clicked.");

        try {
            checkInputs();

            Vehicle vehicle = new Vehicle();

            vehicle.setModel(Model_TF.getText());
            vehicle.setConstruction_date(Integer.parseInt(ConstructionYear_TF.getText()));
            vehicle.setDescription(Description_TF.getText());
            if(!SeatingCapacity_TF.getText().isEmpty()){
                vehicle.setSeating(Integer.parseInt(SeatingCapacity_TF.getText()));
            }
            String License_Type;

            if(LicenseA_RB.isSelected() || LicenseB_RB.isSelected() || LicenseC_RB.isSelected()){
                License_Type = "";

                if(LicenseA_RB.isSelected()){
                    License_Type += "A";
                }
                if(LicenseB_RB.isSelected()){
                    License_Type += "B";
                }
                if(LicenseC_RB.isSelected()){
                    License_Type += "C";
                }
            }else{
                License_Type = "Non";
            }

            vehicle.setLicense(License_Type);
            vehicle.setPlate(Plate_TF.getText());
            vehicle.setType(Motorized_RB.isSelected() ? EngineTypes.Motorized.name() : Brawn_RB.isSelected() ? EngineTypes.Brawn.name() : null);
            if(!EnginePower_TF.getText().isEmpty()){
                vehicle.setPower(Double.parseDouble(EnginePower_TF.getText()));
            }
            vehicle.setPrice(Double.parseDouble(Price_TF.getText()));
            vehicle.setPicture(Photo_TF.getText());

            try {
                fleetService.saveVehicle(vehicle);
                cleanFields();
                LOG.info("Vehicle added");
            } catch (UnsupportedImageException e) {
                LOG.error("Unsupported image selected.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(e.getMessage());
                alert.setContentText("Image size must be smaller than 5Mb and image must be bigger than resolution 500x500 Pixels!");
                alert.showAndWait();
            } catch (FileNotOpenedException e){
                LOG.error("Wrong image path specified.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(e.getMessage());
                alert.setContentText("Image path is wrong. Please check image path again!");
                alert.showAndWait();
            } catch (ServiceException e){
                LOG.error("Database connection error.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(e.getMessage());
                alert.setContentText(e.toString());
                alert.showAndWait();
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

    @FXML
    void OnCancelButtonClicked(ActionEvent event) {
        LOG.info("Vehicle add fields are cleaned.");
        cleanFields();
    }

    @FXML
    void onSelectButtonClicked(ActionEvent actionEvent) {
        LOG.info("Vehicle image selected.");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Image Files", "*.JPEG", "*.PNG", "*.JPG");
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setTitle("Open Image File");
        File file = fileChooser.showOpenDialog(stage.getScene().getWindow());

        if(file != null){
            Photo_TF.setText(file.getPath());
            System.out.println(file.getPath());
        }
    }

    private void checkInputs() throws MandatoryFieldsNotFilledException {
        if(Model_TF.getText().isEmpty()){
            throw new MandatoryFieldsNotFilledException("Model field is empty!");
        }else if(ConstructionYear_TF.getText().isEmpty()){
            throw new MandatoryFieldsNotFilledException("Construction year field is empty!");
        }else if((LicenseA_RB.isSelected() || LicenseB_RB.isSelected() || LicenseC_RB.isSelected()) && Plate_TF.getText().isEmpty()){
            throw new MandatoryFieldsNotFilledException("Plate field is empty!");
        }else if(!Motorized_RB.isSelected() && !Brawn_RB.isSelected()){
            throw new MandatoryFieldsNotFilledException("Engine type is not selected!");
        }else if(Motorized_RB.isSelected() && EnginePower_TF.getText().isEmpty()){
            throw new MandatoryFieldsNotFilledException("Engine power field is empty!");
        }else if(Price_TF.getText().isEmpty()){
            throw new MandatoryFieldsNotFilledException("Price field is empty!");
        }
    }

    private void cleanFields(){
        Model_TF.setText("");
        Description_TF.setText("");
        SeatingCapacity_TF.setText("");
        EnginePower_TF.setText("");
        Price_TF.setText("");
        Photo_TF.setText("");
        ConstructionYear_TF.setText("");
        LicenseA_RB.setSelected(false);
        LicenseB_RB.setSelected(false);
        LicenseC_RB.setSelected(false);
        Plate_TF.setText("");
        Brawn_RB.setSelected(false);
        Motorized_RB.setSelected(false);
    }

    private static void addTextLimiter(final TextField tf, final int maxLength) {
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

    private static void integerTextLimiter(final TextField textField){
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addTextLimiter(Model_TF, 20);
        addTextLimiter(ConstructionYear_TF, 4);
        addTextLimiter(Description_TF, 20);
        addTextLimiter(SeatingCapacity_TF, 2);
        addTextLimiter(Plate_TF, 10);
        addTextLimiter(EnginePower_TF, 5);
        addTextLimiter(Price_TF, 5);
        addTextLimiter(Photo_TF, 100);

        integerTextLimiter(ConstructionYear_TF);
        integerTextLimiter(SeatingCapacity_TF);

        forceDouble();
    }

    private void forceDouble(){
        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        Price_TF.setTextFormatter(formatter);

        TextFormatter formatter2 = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        EnginePower_TF.setTextFormatter(formatter2);
    }
}
