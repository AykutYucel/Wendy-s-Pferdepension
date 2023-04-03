package at.ac.tuwien.sepm.assignment.individual.fleet.entities;

import java.time.LocalDateTime;

public class ChartInformation {
    private double price;
    private LocalDateTime localDateTime;
    private String customerName;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
