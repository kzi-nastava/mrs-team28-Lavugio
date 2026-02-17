package com.example.lavugio_mobile.models;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Matches Angular model:
 *   driverHistory: RideHistoryDriverModel[]
 *   totalElements: number
 *   reachedEnd: boolean
 */
public class RideHistoryDriverPagingModel {
    private List<RideHistoryDriverModel> driverHistory;
    private int totalElements;
    private boolean reachedEnd;

    public RideHistoryDriverPagingModel() {
    }

    public RideHistoryDriverPagingModel(List<RideHistoryDriverModel> driverHistory,
                                        int totalElements, boolean reachedEnd) {
        this.driverHistory = driverHistory;
        this.totalElements = totalElements;
        this.reachedEnd = reachedEnd;
    }

    public List<RideHistoryDriverModel> getDriverHistory() {
        return driverHistory;
    }

    public void setDriverHistory(List<RideHistoryDriverModel> driverHistory) {
        this.driverHistory = driverHistory;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public boolean isReachedEnd() {
        return reachedEnd;
    }

    public void setReachedEnd(boolean reachedEnd) {
        this.reachedEnd = reachedEnd;
    }

    @NonNull
    @Override
    public String toString() {
        return "RideHistoryDriverPagingModel{" +
                "driverHistory=" + (driverHistory != null ? driverHistory.size() + " items" : "null") +
                ", totalElements=" + totalElements +
                ", reachedEnd=" + reachedEnd +
                '}';
    }
}