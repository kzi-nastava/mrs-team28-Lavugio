package com.example.lavugio_mobile.data.model.reports;

import java.util.List;

public class ChartData {
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    private List<String> labels;
    private List<Double> data;
    private double sum;
    private double average;

    public ChartData() {}

    public ChartData(String title, String xAxisLabel, String yAxisLabel,
                     List<String> labels, List<Double> data, double sum, double average) {
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.labels = labels;
        this.data = data;
        this.sum = sum;
        this.average = average;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getXAxisLabel() { return xAxisLabel; }
    public void setXAxisLabel(String xAxisLabel) { this.xAxisLabel = xAxisLabel; }
    public String getYAxisLabel() { return yAxisLabel; }
    public void setYAxisLabel(String yAxisLabel) { this.yAxisLabel = yAxisLabel; }
    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }
    public List<Double> getData() { return data; }
    public void setData(List<Double> data) { this.data = data; }
    public double getSum() { return sum; }
    public void setSum(double sum) { this.sum = sum; }
    public double getAverage() { return average; }
    public void setAverage(double average) { this.average = average; }
}
