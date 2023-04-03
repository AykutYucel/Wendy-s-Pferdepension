package at.ac.tuwien.sepm.assignment.individual.fleet.entities;


import java.sql.Timestamp;

public class Vehicle {
    private int id;
    private String model;
    private int construction_date;
    private String description;
    private int seating;
    private LicenseTypes license;
    private String plate;
    private EngineTypes type;
    private double power;
    private double price;
    private Timestamp creation_time;
    private String picture;
    private Timestamp update_time;
    private boolean isActive;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getConstruction_date() {
        return construction_date;
    }

    public void setConstruction_date(int construction_date) {
        this.construction_date = construction_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSeating() {
        return seating;
    }

    public void setSeating(int seating) {
        this.seating = seating;
    }

    public String getLicense() {
        return license.name();
    }

    public void setLicense(String license) {
        this.license = LicenseTypes.valueOf(license);
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getType() {
        return type.name();
    }

    public void setType(String type) {
        this.type = EngineTypes.valueOf(type);
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Timestamp getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(Timestamp creation_time) {
        this.creation_time = creation_time;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
