package com.example.lavugio_mobile.models;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class NotificationModel {

    @SerializedName("id")
    private long id;

    @SerializedName("link")
    private String link;

    @SerializedName("title")
    private String title;

    @SerializedName("text")
    private String text;

    // LocalDateTime — Gson ga deserijalizuje uz LocalDateTimeAdapter
    @SerializedName("sendDate")
    private LocalDateTime sendDate;

    // ── Getters ──────────────────────────────────────────────

    public long getId()                { return id; }
    public String getLink()            { return link; }
    public String getTitle()           { return title; }
    public String getText()            { return text; }
    public LocalDateTime getSendDate() { return sendDate; }

    // ── Setters ──────────────────────────────────────────────

    public void setId(long id)                      { this.id = id; }
    public void setLink(String link)                { this.link = link; }
    public void setTitle(String title)              { this.title = title; }
    public void setText(String text)                { this.text = text; }
    public void setSendDate(LocalDateTime sendDate) { this.sendDate = sendDate; }
}