package com.example.lavugio_mobile.data.model.reports;

public class RidesReportsDateRangeDTO {
    private String startDate;
    private String endDate;

    public RidesReportsDateRangeDTO() {}

    public RidesReportsDateRangeDTO(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
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
}
