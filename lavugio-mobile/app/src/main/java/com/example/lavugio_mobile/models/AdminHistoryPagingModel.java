package com.example.lavugio_mobile.models;

import java.util.List;

public class AdminHistoryPagingModel {
    private List<AdminHistoryModel> adminHistory;
    private Long totalElements;
    private boolean reachedEnd;

    public AdminHistoryPagingModel() {
    }

    public AdminHistoryPagingModel(List<AdminHistoryModel> adminHistory, Long totalElements, boolean reachedEnd) {
        this.adminHistory = adminHistory;
        this.totalElements = totalElements;
        this.reachedEnd = reachedEnd;
    }

    public List<AdminHistoryModel> getAdminHistory() {
        return adminHistory;
    }

    public void setAdminHistory(List<AdminHistoryModel> adminHistory) {
        this.adminHistory = adminHistory;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public boolean isReachedEnd() {
        return reachedEnd;
    }

    public void setReachedEnd(boolean reachedEnd) {
        this.reachedEnd = reachedEnd;
    }
}
