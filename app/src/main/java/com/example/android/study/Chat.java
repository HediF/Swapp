package com.example.android.study;

public class Chat {
    String sender;
    String receiver;
    String message;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setMsg(String msg) {
        this.message = msg;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMsg() {
        return message;
    }

    public Chat(String sender, String receiver, String msg) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = msg;
    }













}
