package at.ac.tuwien.sepm.assignment.individual.fleet.entities;


public class BookingSearchFilter {
    private String status;

    private boolean statusFilter;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        setStatusFilter(true);
        this.status = status;
    }

    public boolean isStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter(boolean statusFilter) {
        this.statusFilter = statusFilter;
    }

}
