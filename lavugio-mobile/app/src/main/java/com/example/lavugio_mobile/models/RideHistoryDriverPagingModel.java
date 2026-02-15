package com.example.lavugio_mobile.models;

import java.util.List;

public class RideHistoryDriverPagingModel {
    private List<RideHistoryDriverModel> rides;
    private int totalPages;
    private long totalElements;
    private int currentPage;

    public List<RideHistoryDriverModel> getRides() { return rides; }
    public void setRides(List<RideHistoryDriverModel> rides) { this.rides = rides; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
}