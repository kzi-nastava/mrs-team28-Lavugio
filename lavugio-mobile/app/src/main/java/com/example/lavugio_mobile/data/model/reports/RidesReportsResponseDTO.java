package com.example.lavugio_mobile.data.model.reports;

import java.util.List;

public class RidesReportsResponseDTO {
    private List<ChartData> charts;

    public RidesReportsResponseDTO() {}

    public RidesReportsResponseDTO(List<ChartData> charts) {
        this.charts = charts;
    }

    public List<ChartData> getCharts() {
        return charts;
    }

    public void setCharts(List<ChartData> charts) {
        this.charts = charts;
    }
}
