package at.ac.tuwien.sepm.assignment.individual.fleet.entities;

import java.time.LocalDateTime;

public class VehicleSearchFilter {
    private int seating;
    private String model;
    private String  engine;
    private double minPrice;
    private double maxPrice;
    private String license;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    private boolean seatingFilter;
    private boolean modelFilter;
    private boolean engineFilter;
    private boolean PriceFilter;
    private boolean licenseFilter;
    private boolean fromDateFilter;
    private boolean toDateFilter;

    public int getSeating() {
        return seating;
    }

    public void setSeating(int seating) {
        setSeatingFilter(true);
        this.seating = seating;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        setModelFilter(true);
        this.model = model;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        setEngineFilter(true);
        this.engine = engine;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        setPriceFilter(true);
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        setPriceFilter(true);
        this.maxPrice = maxPrice;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        setLicenseFilter(true);
        this.license = license;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        setFromDateFilter(true);
        this.fromDate = fromDate;
    }

    public LocalDateTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalDateTime toDate) {
        setToDateFilter(true);
        this.toDate = toDate;
    }

    public boolean isSeatingFilter() {
        return seatingFilter;
    }

    public void setSeatingFilter(boolean seatingFilter) {
        this.seatingFilter = seatingFilter;
    }

    public boolean isModelFilter() {
        return modelFilter;
    }

    public void setModelFilter(boolean modelFilter) {
        this.modelFilter = modelFilter;
    }

    public boolean isEngineFilter() {
        return engineFilter;
    }

    public void setEngineFilter(boolean engineFilter) {
        this.engineFilter = engineFilter;
    }

    public boolean isPriceFilter() {
        return PriceFilter;
    }

    public void setPriceFilter(boolean priceFilter) {
        PriceFilter = priceFilter;
    }

    public boolean isLicenseFilter() {
        return licenseFilter;
    }

    public void setLicenseFilter(boolean licenseFilter) {
        this.licenseFilter = licenseFilter;
    }

    public boolean isFromDateFilter() {
        return fromDateFilter;
    }

    public void setFromDateFilter(boolean fromDateFilter) {
        this.fromDateFilter = fromDateFilter;
    }

    public boolean isToDateFilter() {
        return toDateFilter;
    }

    public void setToDateFilter(boolean toDateFilter) {
        this.toDateFilter = toDateFilter;
    }

}
