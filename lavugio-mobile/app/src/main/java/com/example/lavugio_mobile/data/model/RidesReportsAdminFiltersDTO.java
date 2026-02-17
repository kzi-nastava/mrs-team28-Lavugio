package com.example.lavugio_mobile.data.model;

import com.example.lavugio_mobile.data.model.reports.RidesReportsAdminFilterEnum;

public class RidesReportsAdminFiltersDTO {
    private String startDate;
    private String endDate;
    private String email;
    private RidesReportsAdminFilterEnum selectedFilter;

    public RidesReportsAdminFiltersDTO() {}

    public RidesReportsAdminFiltersDTO(String startDate, String endDate, String email, RidesReportsAdminFilterEnum selectedFilter) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.email = email;
        this.selectedFilter = selectedFilter;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RidesReportsAdminFilterEnum getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(RidesReportsAdminFilterEnum selectedFilter) {
        this.selectedFilter = selectedFilter;
    }
}
