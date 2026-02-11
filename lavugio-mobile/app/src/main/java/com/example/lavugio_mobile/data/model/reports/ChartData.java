package com.example.lavugio_mobile.data.model.reports;

import java.util.List;

public class ChartData {
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    private List<String> labels;
    private List<Float> data;
    private float sum;
    private float average;

    public ChartData() {}

    public ChartData(String title, String xAxisLabel, String yAxisLabel,
                     List<String> labels, List<Float> data, float sum, float average) {
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
    public List<Float> getData() { return data; }
    public void setData(List<Float> data) { this.data = data; }
    public float getSum() { return sum; }
    public void setSum(float sum) { this.sum = sum; }
    public float getAverage() { return average; }
    public void setAverage(float average) { this.average = average; }
}
