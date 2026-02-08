package com.example.lavugio_mobile.data.model.user;

public class DriverUpdateRequestDiff {
    private Long requestId;
    private DriverUpdateRequest oldData;
    private DriverUpdateRequest newData;
    private String email;

    public DriverUpdateRequestDiff(Long requestId, DriverUpdateRequest oldData, DriverUpdateRequest newData, String email) {
        this.requestId = requestId;
        this.oldData = oldData;
        this.newData = newData;
        this.email = email;
    }

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }
    public DriverUpdateRequest getOldData() { return oldData; }
    public void setOldData(DriverUpdateRequest oldData) { this.oldData = oldData; }
    public DriverUpdateRequest getNewData() { return newData; }
    public void setNewData(DriverUpdateRequest newData) { this.newData = newData; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
