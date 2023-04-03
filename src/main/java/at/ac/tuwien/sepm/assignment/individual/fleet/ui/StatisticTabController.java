package at.ac.tuwien.sepm.assignment.individual.fleet.ui;

import at.ac.tuwien.sepm.assignment.individual.fleet.entities.ChartInformation;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.LicenseTypes;
import at.ac.tuwien.sepm.assignment.individual.fleet.entities.ChartFilter;
import at.ac.tuwien.sepm.assignment.individual.fleet.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.fleet.service.BookingService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class StatisticTabController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    BookingService bookingService;

    public StatisticTabController(BookingService bookingService){
        this.bookingService = bookingService;
    }


    @FXML
    private LineChart<String, Double> LineChart;

    @FXML
    private CategoryAxis X_Axis_Line;

    @FXML
    private NumberAxis Y_Axis_Line;

    @FXML
    private BarChart<DayOfWeek, Double> BarChart;

    @FXML
    private CategoryAxis X_Axis_Bar;

    @FXML
    private NumberAxis Y_Axis_Bar;

    @FXML
    private RadioButton LicenseA;

    @FXML
    private RadioButton LicenseB;

    @FXML
    private RadioButton LicenseC;

    @FXML
    private RadioButton NoLicense;

    @FXML
    private DatePicker fromDate;

    @FXML
    private DatePicker toDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("Statistic tab is initialized.");
        filterListener();
        LineChart.setAnimated(false);
        BarChart.setAnimated(false);
        X_Axis_Line.setLabel("Days");
        Y_Axis_Line.setLabel("Income (€)");
        X_Axis_Bar.setLabel("Days");
        Y_Axis_Bar.setLabel("Number of Renting");
    }

    private void filterListener (){
        fromDate.valueProperty().addListener((observable, oldValue, newValue) ->
            toDate.valueProperty().addListener((observable1, oldValue1, newValue1) ->
                checkDatePicker()
            )
        );

        toDate.valueProperty().addListener((observable, oldValue, newValue) ->
            fromDate.valueProperty().addListener((observable1, oldValue1, newValue1) ->
                checkDatePicker()
            )
        );

        LicenseA.selectedProperty().addListener((observable, oldValue, newValue) ->
            checkDatePicker()
        );

        LicenseB.selectedProperty().addListener((observable, oldValue, newValue) ->
            checkDatePicker()
        );

        LicenseC.selectedProperty().addListener((observable, oldValue, newValue) ->
            checkDatePicker()
        );

        NoLicense.selectedProperty().addListener((observable, oldValue, newValue) ->
            checkDatePicker()
        );
    }

    private void fillLineChartMap(HashMap hashMap){
        LocalDate begin = fromDate.getValue();
        LocalDate end = toDate.getValue();
        while (begin.isBefore(end)){
            hashMap.put(begin, 0.0);
            begin = begin.plusDays(1);
        }
    }

    private void fillBarChartMap(HashMap<DayOfWeek, Integer> hashMap){
        DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
        for(int i = 0; i < 7; i++){
            hashMap.put(dayOfWeek, 0);
            dayOfWeek = dayOfWeek.plus(1);
        }
    }

    private void fillPrice(ChartInformation chartInformation, HashMap<LocalDate, Double> hashMap){
        double temp = hashMap.get(chartInformation.getLocalDateTime().toLocalDate());
        temp += chartInformation.getPrice();
        hashMap.put(chartInformation.getLocalDateTime().toLocalDate(), temp);
    }

    private void fillRenting(ChartInformation chartInformation, HashMap<DayOfWeek, Integer> hashMap){
        int temp = hashMap.get(chartInformation.getLocalDateTime().toLocalDate().getDayOfWeek());
        temp++;
        hashMap.put(chartInformation.getLocalDateTime().toLocalDate().getDayOfWeek(), temp);
    }

    private void setChart() {
        LineChart.getData().clear();
        BarChart.getData().clear();

        List<ChartInformation> chartInfoListA = null;
        List<ChartInformation> chartInfoListB = null;
        List<ChartInformation> chartInfoListC = null;
        List<ChartInformation> chartInfoListNon = null;

        if (LicenseA.isSelected()) {
            LOG.info("Charts are filled for license A.");
            HashMap<LocalDate, Double> lineChartInfoMapLicenseA = new HashMap<>();
            HashMap<DayOfWeek, Integer> barChartInfoMapLicenseA = new HashMap<>();
            fillLineChartMap(lineChartInfoMapLicenseA);
            fillBarChartMap(barChartInfoMapLicenseA);
            if (fromDate.getValue() != null && toDate.getValue() != null) {
                LocalDate localDate = fromDate.getValue();
                while (localDate.isBefore(toDate.getValue())) {
                    String name = "";
                    for (int i = 0; i < 24; i++) {
                        ChartFilter chartFilterA = createChartFilter(Timestamp.valueOf(localDate.atTime(i, 00)), LicenseTypes.A.name());
                        try {
                            chartInfoListA = bookingService.findBooking(chartFilterA);
                        } catch (ServiceException e) {
                            e.printStackTrace();
                        }
                        for (ChartInformation chartInformation : chartInfoListA) {
                            fillPrice(chartInformation, lineChartInfoMapLicenseA);
                            if(!name.equals(chartInformation.getCustomerName())){
                                fillRenting(chartInformation, barChartInfoMapLicenseA);
                                name = chartInformation.getCustomerName();
                            }
                        }
                    }
                    localDate = localDate.plusDays(1);
                }
            }

            XYChart.Series seriesBarChartA = new XYChart.Series();
            seriesBarChartA.setName("License A");
            DayOfWeek dayOfWeek = DayOfWeek.MONDAY;

            for (int i = 0; i < 7; i++) {
                seriesBarChartA.getData().add(new XYChart.Data<>(dayOfWeek.toString(), barChartInfoMapLicenseA.get(dayOfWeek)));
                dayOfWeek = dayOfWeek.plus(1);
            }
            BarChart.getData().addAll(seriesBarChartA);

            XYChart.Series seriesLineChartA = new XYChart.Series();
            seriesLineChartA.setName("License A");
            LocalDate date = fromDate.getValue();

            for (int i = 0; i < lineChartInfoMapLicenseA.size(); i++) {
                seriesLineChartA.getData().add(new XYChart.Data<>(date.toString(), lineChartInfoMapLicenseA.get(date)));
                date = date.plusDays(1);
            }
            LineChart.getData().addAll(seriesLineChartA);
        }

        if (LicenseB.isSelected()) {
            LOG.info("Charts are filled for license B.");
            HashMap<LocalDate, Double> lineChartInfoMapLicenseB = new HashMap<>();
            HashMap<DayOfWeek, Integer> barChartInfoMapLicenseB = new HashMap<>();
            fillLineChartMap(lineChartInfoMapLicenseB);
            fillBarChartMap(barChartInfoMapLicenseB);
            if (fromDate.getValue() != null && toDate.getValue() != null) {
                LocalDate localDate = fromDate.getValue();
                while (localDate.isBefore(toDate.getValue())) {
                    String name = "";
                    for (int i = 0; i < 24; i++) {
                        ChartFilter chartFilterB = createChartFilter(Timestamp.valueOf(localDate.atTime(i, 00)), LicenseTypes.B.name());
                        try {
                            chartInfoListB = bookingService.findBooking(chartFilterB);
                        } catch (ServiceException e) {
                            LOG.error("Database connection error.");
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(e.getMessage());
                            alert.setContentText(e.toString());
                            alert.showAndWait();
                        }
                        for (ChartInformation chartInformation : chartInfoListB) {
                            fillPrice(chartInformation, lineChartInfoMapLicenseB);
                            if(!name.equals(chartInformation.getCustomerName())){
                                fillRenting(chartInformation, barChartInfoMapLicenseB);
                                name = chartInformation.getCustomerName();
                            }
                        }
                    }
                    localDate = localDate.plusDays(1);
                }
            }
            XYChart.Series seriesBarChartB = new XYChart.Series();
            seriesBarChartB.setName("License B");
            DayOfWeek dayOfWeek = DayOfWeek.MONDAY;

            for (int i = 0; i < 7; i++) {
                seriesBarChartB.getData().add(new XYChart.Data<>(dayOfWeek.toString(), barChartInfoMapLicenseB.get(dayOfWeek)));
                dayOfWeek = dayOfWeek.plus(1);
            }
            BarChart.getData().addAll(seriesBarChartB);

            XYChart.Series seriesLineChartB = new XYChart.Series<>();
            seriesLineChartB.setName("License B");
            LocalDate date = fromDate.getValue();

            for (int i = 0; i < lineChartInfoMapLicenseB.size(); i++) {
                seriesLineChartB.getData().add(new XYChart.Data<>(date.toString(), lineChartInfoMapLicenseB.get(date)));
                date = date.plusDays(1);
            }
            LineChart.getData().addAll(seriesLineChartB);
        }

        if (LicenseC.isSelected()) {
            LOG.info("Charts are filled for license C.");
            HashMap<LocalDate, Double> lineChartInfoMapLicenseC = new HashMap<>();
            HashMap<DayOfWeek, Integer> barChartInfoMapLicenseC = new HashMap<>();
            fillLineChartMap(lineChartInfoMapLicenseC);
            fillBarChartMap(barChartInfoMapLicenseC);
            if (fromDate.getValue() != null && toDate.getValue() != null) {
                LocalDate localDate = fromDate.getValue();
                while (localDate.isBefore(toDate.getValue())) {
                    String name = "";
                    for (int i = 0; i < 24; i++) {
                        ChartFilter chartFilterC = createChartFilter(Timestamp.valueOf(localDate.atTime(i, 00)), LicenseTypes.C.name());
                        try {
                            chartInfoListC = bookingService.findBooking(chartFilterC);
                        } catch (ServiceException e) {
                            LOG.error("Database connection error.");
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(e.getMessage());
                            alert.setContentText(e.toString());
                            alert.showAndWait();
                        }
                        for (ChartInformation chartInformation : chartInfoListC) {
                            fillPrice(chartInformation, lineChartInfoMapLicenseC);
                            if(!name.equals(chartInformation.getCustomerName())){
                                fillRenting(chartInformation, barChartInfoMapLicenseC);
                                name = chartInformation.getCustomerName();
                            }
                        }
                    }
                    localDate = localDate.plusDays(1);
                }
            }
            XYChart.Series seriesBarChartC = new XYChart.Series();
            seriesBarChartC.setName("License C");
            DayOfWeek dayOfWeek = DayOfWeek.MONDAY;

            for (int i = 0; i < 7; i++) {
                seriesBarChartC.getData().add(new XYChart.Data<>(dayOfWeek.toString(), barChartInfoMapLicenseC.get(dayOfWeek)));
                dayOfWeek = dayOfWeek.plus(1);
            }
            BarChart.getData().addAll(seriesBarChartC);

            XYChart.Series seriesLineChartC = new XYChart.Series<>();
            seriesLineChartC.setName("License C");
            LocalDate date = fromDate.getValue();

            for (int i = 0; i < lineChartInfoMapLicenseC.size(); i++) {
                seriesLineChartC.getData().add(new XYChart.Data<>(date.toString(), lineChartInfoMapLicenseC.get(date)));
                date = date.plusDays(1);
            }
            LineChart.getData().addAll(seriesLineChartC);
        }

        if (NoLicense.isSelected()) {
            LOG.info("Charts are filled for no license.");
            HashMap<LocalDate, Double> lineChartInfoMapLicenseNon = new HashMap<>();
            HashMap<DayOfWeek, Integer> barChartInfoMapLicenseNon = new HashMap<>();
            fillLineChartMap(lineChartInfoMapLicenseNon);
            fillBarChartMap(barChartInfoMapLicenseNon);
            if (fromDate.getValue() != null && toDate.getValue() != null) {
                LocalDate localDate = fromDate.getValue();
                while (localDate.isBefore(toDate.getValue())) {
                    String name = "";
                    for (int i = 0; i < 24; i++) {
                        ChartFilter chartFilterNon = createChartFilter(Timestamp.valueOf(localDate.atTime(i, 00)), LicenseTypes.Non.name());
                        try {
                            chartInfoListNon = bookingService.findBooking(chartFilterNon);
                        } catch (ServiceException e) {
                            LOG.error("Database connection error.");
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(e.getMessage());
                            alert.setContentText(e.toString());
                            alert.showAndWait();
                        }
                        for (ChartInformation chartInformation : chartInfoListNon) {
                            fillPrice(chartInformation, lineChartInfoMapLicenseNon);
                            if(!name.equals(chartInformation.getCustomerName())){
                                fillRenting(chartInformation, barChartInfoMapLicenseNon);
                                name = chartInformation.getCustomerName();
                            }
                        }
                    }
                    localDate = localDate.plusDays(1);
                }
            }
            XYChart.Series seriesBarChartNon = new XYChart.Series();
            seriesBarChartNon.setName("No License");
            DayOfWeek dayOfWeek = DayOfWeek.MONDAY;

            for (int i = 0; i < 7; i++) {
                seriesBarChartNon.getData().add(new XYChart.Data<>(dayOfWeek.toString(), barChartInfoMapLicenseNon.get(dayOfWeek)));
                dayOfWeek = dayOfWeek.plus(1);
            }
            BarChart.getData().addAll(seriesBarChartNon);

            XYChart.Series seriesLineChartNon = new XYChart.Series<>();
            seriesLineChartNon.setName("No License");
            LocalDate date = fromDate.getValue();
            for (int i = 0; i < lineChartInfoMapLicenseNon.size(); i++) {
                seriesLineChartNon.getData().add(new XYChart.Data<>(date.toString(), lineChartInfoMapLicenseNon.get(date)));
                date = date.plusDays(1);
            }
            LineChart.getData().addAll(seriesLineChartNon);
        }
    }

    private ChartFilter createChartFilter(Timestamp timestamp, String string){
        ChartFilter chartFilter = new ChartFilter();
        if(string.equals("A")){
            chartFilter.setLicenseAfilter(true);
        }else if(string.equals("B")){
            chartFilter.setLicenseBfilter(true);
        }else if(string.equals("C")){
            chartFilter.setLicenseCfilter(true);
        }else{
            chartFilter.setLicenseNonfilter(true);
        }
        if(fromDate.getValue() != null && toDate.getValue() != null){
            chartFilter.setDate(timestamp);
        }
        return chartFilter;
    }

    private void checkDatePicker(){
        if(fromDate.getValue() != null && toDate.getValue() != null){
            if(fromDate.getValue().isAfter(toDate.getValue()) || fromDate.getValue().equals(toDate.getValue())){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Booking date is not correct.");
                alert.setContentText("Beginning date can not be equal or later than ending date!");
                alert.showAndWait();
                fromDate.setValue(null);
                toDate.setValue(null);
                LineChart.getData().clear();
                BarChart.getData().clear();

            }else{
                setChart();
            }
        }
    }
}
