package at.ac.tuwien.sepm.assignment.individual.fleet.ui;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.FleetService;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ViewVehicleDetailsWindowController implements Initializable{

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Vehicle vehicle;
    private FleetService fleetService;
    private final Stage stage;
    private SearchTabController searchTabController;

    public ViewVehicleDetailsWindowController(FleetService fleetService, Stage stage, Vehicle vehicle, SearchTabController searchTabController){
        this.vehicle = vehicle;
        this.fleetService = fleetService;
        this.stage = stage;
        this.searchTabController = searchTabController;
    }


    @FXML
    private TableView<Vehicle> Result_TV;

    @FXML
    private TableColumn<Vehicle, String> Model_TC;

    @FXML
    private TableColumn<Vehicle, Integer> Construction_TC;

    @FXML
    private TableColumn<Vehicle, String> Description_TC;

    @FXML
    private TableColumn<Vehicle, Integer> Seating_TC;

    @FXML
    private TableColumn<Vehicle, String> License_TC;

    @FXML
    private TableColumn Plate_TC;

    @FXML
    private TableColumn<Vehicle, String> Engine_TC;

    @FXML
    private TableColumn<Vehicle, Double> Power_TC;

    @FXML
    private TableColumn<Vehicle, Double> Price_TC;

    @FXML
    private TableColumn<Vehicle, Timestamp> Creation_TC;

    @FXML
    private TableColumn<Vehicle, Timestamp> Edit_TC;

    @FXML
    private ImageView Image_IV;

    @FXML
    void onBookingButtonClicked(ActionEvent event) {
        LOG.info("Book button in vehicle detail is clicked.");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/BookingInDetails.fxml"));
        try {
            BookingInDetailsController bookingInDetailsController = new BookingInDetailsController(vehicle, stage);
            fxmlLoader.setControllerFactory(param -> param.isInstance(bookingInDetailsController) ? bookingInDetailsController : null);
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
    void onDeleteButtonClicked(ActionEvent event) {
        LOG.info("Delete vehicle button in vehicle detail window is clicked.");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you certain that you want to delete this vehicle?");
        alert.setContentText(vehicle.getModel());
        if(alert.showAndWait().get().getButtonData().isDefaultButton()){
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
            Node  source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();
            searchTabController.setTable();
            LOG.info("Vehicle deletion is successful.");
        }
    }

    @FXML
    void onEditButtonClicked(ActionEvent event) {
        LOG.info("Edit button in vehicle detail is clicked.");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/EditVehicleWindow.fxml"));
        try {
            EditVehicleWindowController editVehicleWindowController = new EditVehicleWindowController(fleetService, stage, vehicle, searchTabController);
            fxmlLoader.setControllerFactory(param -> param.isInstance(editVehicleWindowController) ? editVehicleWindowController : null);
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
            Node  source = (Node)  event.getSource();
            Stage current_stage  = (Stage) source.getScene().getWindow();
            current_stage.close();
        } catch (IOException e) {
            LOG.error("FXML file load failed.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("FXML file loading is failed!");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("Vehicle detail window is initialized.");
        Image_IV.setImage(new Image(vehicle.getPicture()));
        ObservableList<Vehicle> list = FXCollections.observableArrayList(vehicle);
        Model_TC.setCellValueFactory(new PropertyValueFactory<>("model"));
        License_TC.setCellValueFactory(new PropertyValueFactory<>("license"));
        Plate_TC.setCellValueFactory(new PropertyValueFactory<>("plate"));
        Engine_TC.setCellValueFactory(new PropertyValueFactory<>("type"));
        Price_TC.setCellValueFactory(new PropertyValueFactory<>("price"));
        Power_TC.setCellValueFactory(new PropertyValueFactory<>("power"));
        Edit_TC.setCellValueFactory(new PropertyValueFactory<>("update_time"));
        Creation_TC.setCellValueFactory(new PropertyValueFactory<>("creation_time"));
        Seating_TC.setCellValueFactory(new PropertyValueFactory<>("seating"));
        Description_TC.setCellValueFactory(new PropertyValueFactory<>("description"));
        Construction_TC.setCellValueFactory(new PropertyValueFactory<>("construction_date"));

        Creation_TC.setCellFactory((TableColumn<Vehicle, Timestamp> column) -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(Timestamp item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm")));
                    }
                }
            };
        });

        Edit_TC.setCellFactory((TableColumn<Vehicle, Timestamp> column) -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(Timestamp item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm")));
                    }
                }
            };
        });

        Result_TV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Result_TV.setItems(list);
    }
}
