package com.example.lavugio_mobile.models;

import com.google.gson.annotations.SerializedName;

public class UserChatModel {

    @SerializedName("userId")
    private int userId;

    @SerializedName("email")
    private String email;

    public UserChatModel() {}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}