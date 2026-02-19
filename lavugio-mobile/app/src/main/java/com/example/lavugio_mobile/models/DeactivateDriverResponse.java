package com.example.lavugio_mobile.models;

public class DeactivateDriverResponse {
    private String message;
    private Boolean pending;
    private Boolean active;
    private boolean success;

    public static DeactivateDriverResponse failure() {
        DeactivateDriverResponse r = new DeactivateDriverResponse();
        r.success = false;
        return r;
    }

    public String getMessage() { return message; }
    public Boolean getPending() { return pending; }
    public Boolean getActive() { return active; }
    public boolean isPending() { return Boolean.TRUE.equals(pending); }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
