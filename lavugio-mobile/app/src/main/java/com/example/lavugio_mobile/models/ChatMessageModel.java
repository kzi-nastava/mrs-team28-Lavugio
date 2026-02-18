package com.example.lavugio_mobile.models;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class ChatMessageModel {

    @SerializedName("senderId")
    private int senderId;

    @SerializedName("receiverId")
    private int receiverId;

    @SerializedName("text")
    private String text;

    @SerializedName("timestamp")
    private LocalDateTime timestamp;

    public ChatMessageModel() {}

    public ChatMessageModel(int senderId, int receiverId, String text, LocalDateTime timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}