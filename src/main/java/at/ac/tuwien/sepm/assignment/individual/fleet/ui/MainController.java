package at.ac.tuwien.sepm.assignment.individual.fleet.ui;

import at.ac.tuwien.sepm.assignment.individual.fleet.service.FleetService;

import javafx.event.Event;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class MainController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FleetService fleetService;
    private SearchTabController searchTabController;
    private BookingAndInvoiceTabController bookingAndInvoiceTabController;


    public MainController(FleetService fleetService, SearchTabController searchTabController, BookingAndInvoiceTabController bookingAndInvoiceTabController) {
        this.fleetService = fleetService;
        this.searchTabController = searchTabController;
        this.bookingAndInvoiceTabController = bookingAndInvoiceTabController;

    }

    @FXML
    public void onSearchVehicleTabClicked(Event event) {
        LOG.info("Search vehicle tab is clicked.");
        searchTabController.setTable();
    }

    @FXML
    public void onBookingAndInvoiceTabClicked(Event event) {
        LOG.info("Booking & Invoice tab is clicked.");
        bookingAndInvoiceTabController.setTable();
    }

}
