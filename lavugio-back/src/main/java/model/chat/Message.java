package model.chat;

import model.user.AccountType;

import java.time.LocalDate;
import java.time.LocalTime;

public class Message {
    private long id;
    private long senderId;
    private LocalDate messageDate;
    private LocalTime messageTime;
    private AccountType senderType;
    private String text;

    public Message() {

    }

    public Message(LocalDate messageDate, LocalTime messageTime, AccountType senderType, String text) {
        this.messageDate = messageDate;
        this.messageTime = messageTime;
        this.senderType = senderType;
        this.text = text;
    }

    public Message(long id, LocalDate messageDate, LocalTime messageTime, AccountType senderType, String text) {
        this.id = id;
        this.messageDate = messageDate;
        this.messageTime = messageTime;
        this.senderType = senderType;
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(LocalDate messageDate) {
        this.messageDate = messageDate;
    }

    public LocalTime getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(LocalTime messageTime) {
        this.messageTime = messageTime;
    }

    public AccountType getSenderType() {
        return senderType;
    }

    public void setSenderType(AccountType senderType) {
        this.senderType = senderType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
