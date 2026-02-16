package com.example.lavugio_mobile.models.user;

public class DriverUpdateRequestDiffDTO {
    private Long requestId;
    private DriverEditProfileRequestDTO oldData;
    private DriverEditProfileRequestDTO newData;
    private String email;

    public DriverUpdateRequestDiffDTO() {
    }

    public DriverUpdateRequestDiffDTO(Long requestId, DriverEditProfileRequestDTO oldData, DriverEditProfileRequestDTO newData, String email) {
        this.requestId = requestId;
        this.oldData = oldData;
        this.newData = newData;
        this.email = email;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public DriverEditProfileRequestDTO getOldData() {
        return oldData;
    }

    public void setOldData(DriverEditProfileRequestDTO oldData) {
        this.oldData = oldData;
    }

    public DriverEditProfileRequestDTO getNewData() {
        return newData;
    }

    public void setNewData(DriverEditProfileRequestDTO newData) {
        this.newData = newData;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
