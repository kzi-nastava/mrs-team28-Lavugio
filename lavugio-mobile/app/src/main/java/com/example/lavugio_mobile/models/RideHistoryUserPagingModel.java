package com.example.lavugio_mobile.models;

import java.util.List;

/**
 * Model for paginated user ride history response.
 */
public class RideHistoryUserPagingModel {
    private List<RideHistoryUserModel> userHistory;
    private boolean reachedEnd;

    public List<RideHistoryUserModel> getUserHistory() {
        return userHistory;
    }

    public void setUserHistory(List<RideHistoryUserModel> userHistory) {
        this.userHistory = userHistory;
    }

    public boolean isReachedEnd() {
        return reachedEnd;
    }

    public void setReachedEnd(boolean reachedEnd) {
        this.reachedEnd = reachedEnd;
    }
}
