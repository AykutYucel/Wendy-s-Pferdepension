package at.ac.tuwien.sepm.assignment.individual.fleet.entities;

import java.sql.Timestamp;

public class ChartFilter {

    private Timestamp date;

    private boolean LicenseAfilter;
    private boolean LicenseBfilter;
    private boolean LicenseCfilter;
    private boolean LicenseNonfilter;
    private boolean dateFilter;

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        setDateFilter(true);
        this.date = date;
    }

    public boolean isLicenseAfilter() {
        return LicenseAfilter;
    }

    public void setLicenseAfilter(boolean licenseAfilter) {
        LicenseAfilter = licenseAfilter;
    }

    public boolean isLicenseBfilter() {
        return LicenseBfilter;
    }

    public void setLicenseBfilter(boolean licenseBfilter) {
        LicenseBfilter = licenseBfilter;
    }

    public boolean isLicenseCfilter() {
        return LicenseCfilter;
    }

    public void setLicenseCfilter(boolean licenseCfilter) {
        LicenseCfilter = licenseCfilter;
    }

    public boolean isLicenseNonfilter() {
        return LicenseNonfilter;
    }

    public void setLicenseNonfilter(boolean licenseNonfilter) {
        LicenseNonfilter = licenseNonfilter;
    }

    public boolean isDateFilter() {
        return dateFilter;
    }

    public void setDateFilter(boolean dateFilter) {
        this.dateFilter = dateFilter;
    }
}