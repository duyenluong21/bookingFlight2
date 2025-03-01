package com.example.bookingflight.model;

public class ChatBot {
    private String message;
    private boolean isUser;
    private String messageId;

    public ChatBot(){

    }

    public ChatBot(String message, boolean isUser, String messageId) {
        this.message = message;
        this.isUser = isUser;
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
