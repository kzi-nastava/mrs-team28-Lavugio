package model.ride;

public class RideReport {
    private long reportId;
    private long rideId;
    private String reportMessage;

    public RideReport() {

    }

    public RideReport(long reportId, long rideId, String reportMessage) {
        this.reportId = reportId;
        this.rideId = rideId;
        this.reportMessage = reportMessage;
    }

    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    public long getRideId() {
        return rideId;
    }

    public void setRideId(long rideId) {
        this.rideId = rideId;
    }

    public String getReportMessage() {
        return reportMessage;
    }

    public void setReportMessage(String reportMessage) {
        this.reportMessage = reportMessage;
    }
}
