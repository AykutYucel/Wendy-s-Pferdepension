package at.ac.tuwien.sepm.assignment.individual.fleet.ui;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.Booking;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.BookingSearchFilter;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.BookingStatus;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.CancelingBookingServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.CancellationFeeException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.CompleteBookingServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.BookingService;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.BookingVehicleService;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.SimpleBookingVehicleService;
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
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class BookingAndInvoiceTabController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private BookingService bookingService;
    private BookingVehicleService bookingVehicleService;

    public BookingAndInvoiceTabController(BookingService bookingService, BookingVehicleService bookingVehicleService){
        this.bookingService = bookingService;
        this.bookingVehicleService= bookingVehicleService;
    }

    @FXML
    private TableView<Booking> Booking_TV;

    @FXML
    private TableColumn<Booking, String> Name_TC;

    @FXML
    private TableColumn<Booking, LocalDateTime> Beginning_TC;

    @FXML
    private TableColumn<Booking, LocalDateTime> Ending_TC;

    @FXML
    private TableColumn<Booking, String> Status_TC;

    @FXML
    private TableColumn<Booking, Double> Price_TC;

    @FXML
    private ChoiceBox Type_CB;

    @FXML
    private Button Complete_Button;

    @FXML
    private Button Detail_Button;

    @FXML
    private Button Cancel_Button;

    @FXML
    private Button Edit_Button;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("Booking & Invoice Tab is initialized.");
        Type_CB.setItems(FXCollections.observableArrayList("All", "Booking", "Invoice"));
        Type_CB.getSelectionModel().selectFirst();
        setTable();
        filterListener();
        tableViewListener(Booking_TV);
    }

    public void setTable(){
        LOG.info("Booking & Invoice table view created.");
        Complete_Button.setDisable(true);
        Detail_Button.setDisable(true);
        Cancel_Button.setDisable(true);
        Edit_Button.setDisable(true);

        ObservableList<Booking> list = null;
        try {
            list = FXCollections.observableArrayList(bookingService.findBooking(bookingSearchFilter()));
        } catch (ServiceException e) {
            LOG.error("Database connection error.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.setContentText(e.toString());
            alert.showAndWait();
        }

        Name_TC.setCellValueFactory(new PropertyValueFactory<>("name"));
        Beginning_TC.setCellValueFactory(new PropertyValueFactory<>("fromDate"));
        Ending_TC.setCellValueFactory(new PropertyValueFactory<>("toDate"));
        Status_TC.setCellValueFactory(new PropertyValueFactory<>("status"));
        Price_TC.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        Beginning_TC.setCellFactory((TableColumn<Booking, LocalDateTime> column) -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm")));
                    }
                }
            };
        });
        Ending_TC.setCellFactory((TableColumn<Booking, LocalDateTime> column) -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm")));
                    }
                }
            };
        });

        Booking_TV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        Booking_TV.setItems(list);
    }

    private BookingSearchFilter bookingSearchFilter(){
        BookingSearchFilter bookingSearchFilter = new BookingSearchFilter();
        if(Type_CB.getSelectionModel().getSelectedIndex() == 0){
            bookingSearchFilter.setStatusFilter(false);
        }else if(Type_CB.getSelectionModel().getSelectedIndex() == 1){
            bookingSearchFilter.setStatus("Booking");
        }else{
            bookingSearchFilter.setStatus("Invoice");
        }

        return bookingSearchFilter;
    }

    private void filterListener (){
        Type_CB.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
            setTable()
        );
    }

    private void tableViewListener(TableView tableView){
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null){
                Complete_Button.setDisable(false);
                Detail_Button.setDisable(false);
                Cancel_Button.setDisable(false);
                Edit_Button.setDisable(false);
            }
        });
    }

    @FXML
    void onCancelButtonClicked(ActionEvent event) {
        LOG.info("Booking cancel button is clicked.");
        Booking booking = Booking_TV.getSelectionModel().getSelectedItem();
        if(booking.getStatus().equals(BookingStatus.canceled) || booking.getStatus().equals(BookingStatus.completed)){
            LOG.error("Invoices can not be canceled.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Booking cancellation is failed");
            alert.setContentText("Invoices can not be canceled!");
            alert.showAndWait();
            return;
        }
        try{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Cancellation!");
            alert.setHeaderText("Are you certain that you want to cancel this booking?");
            alert.setContentText(Booking_TV.getSelectionModel().getSelectedItem().getName());
            if(alert.showAndWait().get().getButtonData().isDefaultButton()){
                try {
                    bookingService.cancelBooking(booking, false);
                } catch (ServiceException e) {
                    LOG.error("Database connection error.");
                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setTitle("Error");
                    alert2.setHeaderText(e.getMessage());
                    alert2.setContentText(e.toString());
                    alert2.showAndWait();
                }
                setTable();
            }


        }catch (CancelingBookingServiceException e){
            LOG.error("Cancellation period is over for this booking.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Booking cancellation is failed");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }catch (CancellationFeeException e){
            LOG.error("Booking isn't in free cancellation period.");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Cancellation!");
            alert.setHeaderText(e.getMessage());
            if(alert.showAndWait().get().getButtonData().isDefaultButton()){
                try {
                    double price = booking.getTotalPrice();
                    if(e.getMessage().contains("%40")){
                        price = price * 40 / 100;
                    }else{
                        price = price * 75 / 100;
                    }
                    booking.setTotalPrice(price);
                    try {
                        bookingService.cancelBooking(booking, true);
                    } catch (ServiceException e1) {
                        LOG.error("Database connection error.");
                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                        alert2.setTitle("Error");
                        alert2.setHeaderText(e.getMessage());
                        alert2.setContentText(e.toString());
                        alert2.showAndWait();
                    }
                    setTable();
                    LOG.info("Booking cancellation is successful.");
                } catch (CancelingBookingServiceException e1) {
                    Alert alert1 = new Alert(Alert.AlertType.ERROR);
                    alert1.setTitle("Error");
                    alert1.setHeaderText("Booking cancellation is failed");
                    alert1.setContentText(e1.getMessage());
                    alert1.showAndWait();
                } catch (CancellationFeeException e1) {
                    Alert alert1 = new Alert(Alert.AlertType.ERROR);
                    alert1.setTitle("Error");
                    alert1.setHeaderText("Booking cancellation is failed");
                    alert1.setContentText(e1.getMessage());
                    alert1.showAndWait();
                }
            }
        }
    }

    @FXML
    void onCompleteButtonClicked(ActionEvent event) {
        LOG.info("Booking complete button is clicked.");
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Completion!");
            alert.setHeaderText("Are you certain that you want to complete this booking?");
            alert.setContentText(Booking_TV.getSelectionModel().getSelectedItem().getName());
            if(alert.showAndWait().get().getButtonData().isDefaultButton()){
                try {
                    bookingService.completeBooking(Booking_TV.getSelectionModel().getSelectedItem());
                } catch (ServiceException e) {
                    LOG.error("Database connection error.");
                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setTitle("Error");
                    alert2.setHeaderText(e.getMessage());
                    alert2.setContentText(e.toString());
                    alert2.showAndWait();
                }
                setTable();
                LOG.info("Booking completion is successful.");
            }
        }catch (CompleteBookingServiceException e){
            LOG.error("Invoice can not be completed.");
            Alert alert1 = new Alert(Alert.AlertType.ERROR);
            alert1.setTitle("Error");
            alert1.setHeaderText("Booking completion is failed");
            alert1.setContentText(e.getMessage());
            alert1.showAndWait();
        }
    }

    @FXML
    void onDetailsButtonClicked(ActionEvent event) {
        LOG.info("Booking & Invoice detail is clicked.");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ViewBookingDetails.fxml"));
        try {
            BookingVehicleService bookingVehicleService = new SimpleBookingVehicleService();
            ViewBookingDetailsController viewBookingDetailsController = new ViewBookingDetailsController(Booking_TV.getSelectionModel().getSelectedItem(), bookingVehicleService);
            fxmlLoader.setControllerFactory(param -> param.isInstance(viewBookingDetailsController) ? viewBookingDetailsController : null);
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
    void onEditButtonClicked(ActionEvent event) {
        LOG.info("Booking edit button is clicked.");
        if(Booking_TV.getSelectionModel().getSelectedItem().getStatus().equals(BookingStatus.open)){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/EditBookingWindow.fxml"));
            try {
                EditBookingWindowController editBookingWindowController = new EditBookingWindowController(this, Booking_TV.getSelectionModel().getSelectedItem(), bookingVehicleService, bookingService);
                fxmlLoader.setControllerFactory(param -> param.isInstance(editBookingWindowController) ? editBookingWindowController : null);
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
        }else{
            LOG.error("Invoice can not be edited.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Editing is failed!");
            alert.setContentText("Invoice editing is not possible!");
            alert.showAndWait();
        }
    }
}
