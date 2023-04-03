package at.ac.tuwien.sepm.assignment.individual.fleet.ui;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Booking;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.VehicleSearchFilter;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.BookingService;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.BookingVehicleService;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.FleetService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;


public class SearchTabController implements Initializable{

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FleetService fleetService;
    private final BookingService bookingService;
    private final BookingVehicleService bookingVehicleService;
    private final Stage stage;



    public SearchTabController(FleetService fleetService, BookingService bookingService, Stage stage, BookingVehicleService bookingVehicleService) {
        this.fleetService = fleetService;
        this.stage = stage;
        this.bookingService = bookingService;
        this.bookingVehicleService = bookingVehicleService;
    }

    @FXML
    private ChoiceBox Beginning_CB;

    @FXML
    private ChoiceBox Ending_CB;

    @FXML
    private Button BookButton;

    @FXML
    private Button EditButton;

    @FXML
    private Button DeleteButton;

    @FXML
    private Button ViewDetailsButton;

    @FXML
    private TableView<Vehicle> Result_TV;

    @FXML
    private TableColumn<Vehicle, String> Model_TC;

    @FXML
    private TableColumn<Vehicle, Integer> License_TC;

    @FXML
    private TableColumn<Vehicle, Boolean> Engine_TC;

    @FXML
    private TableColumn<Vehicle, Double> Price_TC;

    @FXML
    private TextField MinPrice_TF;

    @FXML
    private TextField MaxPrice_TF;

    @FXML
    private DatePicker Beginning_DP;

    @FXML
    private DatePicker Ending_DP;

    @FXML
    private TextField Model_TF;

    @FXML
    private ChoiceBox LicenseMenu_CB;

    @FXML
    private ChoiceBox EngineType_CB;

    @FXML
    private TextField SeatingCapacity_TF;

    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        LOG.info("Search filters are cleaned.");
        MinPrice_TF.setText("");
        MaxPrice_TF.setText("");
        Beginning_DP.setValue(null);
        Ending_DP.setValue(null);
        Model_TF.setText("");
        LicenseMenu_CB.setValue("All");
        EngineType_CB.setValue("All");
        SeatingCapacity_TF.setText("");
        Beginning_CB.setValue("00:00");
        Ending_CB.setValue("00:00");
    }

    @FXML
    public void onDeleteButtonClicked(ActionEvent actionEvent) {
        LOG.info("Delete vehicle button is clicked.");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        String deletedCars = "";
        for(Vehicle vehicle : Result_TV.getSelectionModel().getSelectedItems()){
            deletedCars += vehicle.getModel() + "\n";
        }
        alert.setHeaderText("Are you certain that you want to delete selected vehicle(s)?");
        alert.setContentText(deletedCars);
        if(alert.showAndWait().get().getButtonData().isDefaultButton()){
            Iterator iterator = Result_TV.getSelectionModel().getSelectedItems().iterator();
            while (iterator.hasNext()){
                Vehicle vehicle = (Vehicle) iterator.next();
                try {
                    fleetService.deleteVehicle(vehicle);
                } catch (ServiceException e) {
                    LOG.error("Database connection error.");
                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setTitle("Error");
                    alert2.setHeaderText(e.getMessage());
                    alert2.setContentText(e.toString());
                    alert2.showAndWait();
                }
            }
            setTable();
            LOG.info("Vehicle deletion is successful.");
        }

    }

    @FXML
    public void onEditButtonClicked(ActionEvent actionEvent) {
        LOG.info("Vehicle edit button is clicked.");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/EditVehicleWindow.fxml"));
        try {
            EditVehicleWindowController editVehicleWindowController = new EditVehicleWindowController(fleetService, stage, Result_TV.getSelectionModel().getSelectedItem(), this);
            fxmlLoader.setControllerFactory(param -> param.isInstance(editVehicleWindowController) ? editVehicleWindowController : null);
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOG.error("FXML file load failed.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("FXML file loading is failed!");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    @FXML
    public void onViewDetailsButtonClicked(ActionEvent actionEvent) {
        LOG.info("Vehicle detail button is clicked.");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ViewVehicleDetailsWindow.fxml"));
        try {
            ViewVehicleDetailsWindowController viewVehicleDetailsWindowController = new ViewVehicleDetailsWindowController(fleetService, stage, Result_TV.getSelectionModel().getSelectedItem(), this);
            fxmlLoader.setControllerFactory(param -> param.isInstance(viewVehicleDetailsWindowController) ? viewVehicleDetailsWindowController : null);
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOG.error("FXML file load failed.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("FXML file loading is failed!");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    @FXML
    public void onBookButtonClicked(ActionEvent actionEvent) {
        LOG.info("Vehicle book button is clicked.");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        LocalTime begTime = LocalTime.of(Integer.parseInt(Beginning_CB.getValue().toString().substring(0,2)), Integer.parseInt(Beginning_CB.getValue().toString().substring(3,5)));
        LocalTime endTime = LocalTime.of(Integer.parseInt(Ending_CB.getValue().toString().substring(0,2)), Integer.parseInt(Ending_CB.getValue().toString().substring(3,5)));
        LocalDateTime begDateTime;
        LocalDateTime endDateTime;

        if(Beginning_DP.getValue() != null || Ending_DP.getValue() != null){
            begDateTime = LocalDateTime.of(Beginning_DP.getValue(), begTime);
            endDateTime = LocalDateTime.of(Ending_DP.getValue(), endTime);
        }else{
            alert.setTitle("Error");
            alert.setHeaderText("Booking period is not selected.");
            alert.setContentText("Please specify the booking date first!");
            alert.showAndWait();
            return;
        }
        if(begDateTime.isBefore(LocalDateTime.now())){
            alert.setTitle("Error");
            alert.setHeaderText("Booking date is not correct.");
            alert.setContentText("Beginning date can not be earlier than current time!");
            alert.showAndWait();
        }else if(begDateTime.equals(endDateTime) || begDateTime.isAfter(endDateTime)){
            alert.setTitle("Error");
            alert.setHeaderText("Booking date is not correct.");
            alert.setContentText("Beginning date can not be equal or later than ending date!");
            alert.showAndWait();
        }else{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/BookingWindow.fxml"));
            try {
                Booking booking = new Booking();
                booking.setFromDate(begDateTime);
                booking.setToDate(endDateTime);
                List<Vehicle> vehicleList = Result_TV.getSelectionModel().getSelectedItems();
                BookingWindowController bookingWindowController = new BookingWindowController(this, booking, vehicleList, bookingService, bookingVehicleService);
                fxmlLoader.setControllerFactory(param -> param.isInstance(bookingWindowController) ? bookingWindowController : null);
                Parent root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.initStyle(StageStyle.DECORATED);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e){
                LOG.error("FXML file load failed.");
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setTitle("Error");
                alert2.setHeaderText("FXML file loading is failed!");
                alert2.setContentText(e.toString());
                alert2.showAndWait();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("Search tab is initialized.");
        tableViewListener(Result_TV);
        fillMenuButtons();
        setTable();
        filterListener();
        addTextLimiter(Model_TF, 20);
        addTextLimiter(MinPrice_TF, 4);
        addTextLimiter(MaxPrice_TF, 4);
        addTextLimiter(SeatingCapacity_TF, 2);

        integerTextLimiter(SeatingCapacity_TF);

        forceDouble();
    }

    private void tableViewListener(TableView tableView){
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null){
                EditButton.setDisable(false);
                DeleteButton.setDisable(false);
                ViewDetailsButton.setDisable(false);
                BookButton.setDisable(false);
            }
            if(Result_TV.getSelectionModel().getSelectedItems().size() > 1){
                EditButton.setDisable(true);
                ViewDetailsButton.setDisable(true);
            }
        });
    }

    private void checkDatePicker(){
        if(Beginning_DP.getValue() != null && Ending_DP.getValue() != null){
            LocalTime begTime = LocalTime.of(Integer.parseInt(Beginning_CB.getValue().toString().substring(0,2)), Integer.parseInt(Beginning_CB.getValue().toString().substring(3,5)));
            LocalTime endTime = LocalTime.of(Integer.parseInt(Ending_CB.getValue().toString().substring(0,2)), Integer.parseInt(Ending_CB.getValue().toString().substring(3,5)));

            LocalDateTime begin = LocalDateTime.of(Beginning_DP.getValue(), begTime);
            LocalDateTime end = LocalDateTime.of(Ending_DP.getValue(), endTime);
            if(begin.isAfter(end) || begin.equals(end)){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Booking date is not correct.");
                alert.setContentText("Beginning date can not be equal or later than ending date!");
                alert.showAndWait();
                Beginning_DP.setValue(null);
                Ending_DP.setValue(null);
                setTable();
            }else{
                setTable();
            }
        }else{
            setTable();
        }
    }

    private void filterListener (){

        MinPrice_TF.textProperty().addListener((observable, oldValue, newValue) ->
            setTable()
        );
        MaxPrice_TF.textProperty().addListener((observable, oldValue, newValue) ->
            setTable()
        );
        Beginning_DP.valueProperty().addListener((observable, oldValue, newValue) ->
            checkDatePicker()
        );
        Ending_DP.valueProperty().addListener((observable, oldValue, newValue) ->
            checkDatePicker()
        );
        Model_TF.textProperty().addListener((observable, oldValue, newValue) ->
            setTable()
        );
        LicenseMenu_CB.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
            setTable()
        );
        EngineType_CB.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
            setTable()
        );
        SeatingCapacity_TF.textProperty().addListener((observable, oldValue, newValue) ->
            setTable()
        );

        Beginning_CB.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
            setTable()
        );

        Ending_CB.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
            setTable()
        );
    }

    public void setTable(){
        ObservableList<Vehicle> list = null;
        try {
            list = FXCollections.observableArrayList(fleetService.findVehicle(createFilter()));
        } catch (ServiceException e) {
            LOG.error("Database connection error.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.setContentText(e.toString());
            alert.showAndWait();
        }

        Model_TC.setCellValueFactory(new PropertyValueFactory<>("model"));
        License_TC.setCellValueFactory(new PropertyValueFactory<>("license"));
        Engine_TC.setCellValueFactory(new PropertyValueFactory<>("type"));
        Price_TC.setCellValueFactory(new PropertyValueFactory<>("price"));

        Result_TV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Result_TV.setItems(list);
        EditButton.setDisable(true);
        DeleteButton.setDisable(true);
        ViewDetailsButton.setDisable(true);
        BookButton.setDisable(true);
    }

    private void fillMenuButtons(){
        LicenseMenu_CB.setItems(FXCollections.observableArrayList("All", "A", "B", "C", "No License"));
        EngineType_CB.setItems(FXCollections.observableArrayList("All", "Motorized", "Brawn"));
        Beginning_CB.setItems(FXCollections.observableArrayList("00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"));
        Ending_CB.setItems(FXCollections.observableArrayList("00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"));


        LicenseMenu_CB.getSelectionModel().selectFirst();
        EngineType_CB.getSelectionModel().selectFirst();
        Beginning_CB.getSelectionModel().selectFirst();
        Ending_CB.getSelectionModel().selectFirst();

    }

    private VehicleSearchFilter createFilter(){
        VehicleSearchFilter vehicleSearchFilter = new VehicleSearchFilter();
        if(!SeatingCapacity_TF.getText().isEmpty()){
            vehicleSearchFilter.setSeating(Integer.parseInt(SeatingCapacity_TF.getText()));
        }
        if(!LicenseMenu_CB.getSelectionModel().isEmpty()){
            if(LicenseMenu_CB.getSelectionModel().getSelectedIndex() == 4){
                vehicleSearchFilter.setLicense("Non");
            }
            else if(LicenseMenu_CB.getSelectionModel().getSelectedIndex() != 0){
                if(LicenseMenu_CB.getSelectionModel().getSelectedIndex() == 1){
                    vehicleSearchFilter.setLicense("A");
                }else if(LicenseMenu_CB.getSelectionModel().getSelectedIndex() == 2){
                    vehicleSearchFilter.setLicense("B");
                }else if(LicenseMenu_CB.getSelectionModel().getSelectedIndex() == 3){
                    vehicleSearchFilter.setLicense("C");
                }
            }

        }
        if(!MinPrice_TF.getText().isEmpty()){
            vehicleSearchFilter.setMinPrice(Double.parseDouble(MinPrice_TF.getText()));
        }
        if(!MaxPrice_TF.getText().isEmpty()){
            vehicleSearchFilter.setMaxPrice(Double.parseDouble(MaxPrice_TF.getText()));
        }
        if(!Model_TF.getText().isEmpty()){
            vehicleSearchFilter.setModel(Model_TF.getText());
        }
        if(!EngineType_CB.getSelectionModel().isEmpty()){
            if(EngineType_CB.getSelectionModel().getSelectedIndex() != 0){
                if(EngineType_CB.getSelectionModel().getSelectedIndex() == 1){
                    vehicleSearchFilter.setEngine("Motorized");
                }else if(EngineType_CB.getSelectionModel().getSelectedIndex() == 2){
                    vehicleSearchFilter.setEngine("Brawn");
                }
            }else{
                vehicleSearchFilter.setEngineFilter(false);
            }
        }
        if(Beginning_DP.getValue() != null){
            String time = getChoiceBoxTime(Beginning_CB);
            int hour = Integer.parseInt(time.substring(0,2));
            int min = Integer.parseInt(time.substring(3,5));
            LocalDateTime localDateTime = Beginning_DP.getValue().atTime(hour,min);
            vehicleSearchFilter.setFromDate(localDateTime);

        }
        if(Ending_DP.getValue() != null){
            String time = getChoiceBoxTime(Ending_CB);
            int hour = Integer.parseInt(time.substring(0,2));
            int min = Integer.parseInt(time.substring(3,5));
            LocalDateTime localDateTime = Ending_DP.getValue().atTime(hour,min);
            vehicleSearchFilter.setToDate(localDateTime);
        }

        return vehicleSearchFilter;
    }

    private String getChoiceBoxTime(ChoiceBox choiceBox){
        if(choiceBox.getSelectionModel().getSelectedIndex() == 0){
            return "00:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 1){
            return "01:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 2){
            return "02:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 3){
            return "03:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 4){
            return "04:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 5){
            return "05:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 6){
            return "06:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 7){
            return "07:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 8){
            return "08:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 9){
            return "09:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 10){
            return "10:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 11){
            return "11:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 12){
            return "12:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 13){
            return "13:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 14){
            return "14:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 15){
            return "15:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 16){
            return "16:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 17){
            return "17:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 18){
            return "18:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 19){
            return "19:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 20){
            return "20:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 21){
            return "21:00";
        }else if(choiceBox.getSelectionModel().getSelectedIndex() == 22){
            return "22:00";
        }else{
            return "23:00";
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

    private void forceDouble(){
        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        MinPrice_TF.setTextFormatter(formatter);

        TextFormatter formatter2 = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        MaxPrice_TF.setTextFormatter(formatter2);
    }
}
