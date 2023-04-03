package at.ac.tuwien.sepm.assignment.individual.application;

import at.ac.tuwien.sepm.assignment.individual.fleet.service.*;
import at.ac.tuwien.sepm.assignment.individual.fleet.ui.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public final class MainApplication extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void start(Stage primaryStage) throws Exception {
        // setup application
        primaryStage.setTitle("Rent Service");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(event -> LOG.debug("Application shutdown initiated"));

        // initiate service and controller
        FleetService fleetService = new SimpleFleetService();
        BookingService bookingService = new SimpleBookingService();
        BookingVehicleService bookingVehicleService = new SimpleBookingVehicleService();
        SearchTabController searchTabController = new SearchTabController(fleetService, bookingService, primaryStage, bookingVehicleService);
        AddTabController addTabController = new AddTabController(fleetService, primaryStage);
        BookingAndInvoiceTabController bookingAndInvoiceTabController = new BookingAndInvoiceTabController(bookingService, bookingVehicleService);
        StatisticTabController statisticTabController = new StatisticTabController(bookingService);
        MainController mainController = new MainController(fleetService, searchTabController, bookingAndInvoiceTabController);


        // prepare fxml loader to inject controller
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainApplication.fxml"));
        fxmlLoader.setControllerFactory(param -> param.isInstance(mainController) ? mainController : param.isInstance(addTabController) ? addTabController : param.isInstance(searchTabController) ? searchTabController : param.isInstance(bookingAndInvoiceTabController) ? bookingAndInvoiceTabController : param.isInstance(statisticTabController) ? statisticTabController : null);
        primaryStage.setScene(new Scene(fxmlLoader.load()));

        // show application
        primaryStage.show();
        primaryStage.toFront();
        LOG.debug("Application startup complete");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

            }
        });
    }

    public static void main(String[] args) {
        LOG.debug("Application starting with arguments={}", (Object) args);
        Application.launch(MainApplication.class, args);
    }

}
