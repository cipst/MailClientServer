package com.project.models;

import java.util.ArrayList;

public class EmailSerializable implements java.io.Serializable {

    private int id;
    private String sender;
    private ArrayList<String> recipients;
    private String subject;
    private String message;

    private String date;

    public EmailSerializable(int id, String sender, ArrayList<String> recipients, String subject, String message, String date) {
        this.id = id;
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.message = message;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public ArrayList<String> getRecipients() {
        return recipients;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("Sender: %s - Recipients: %s - Subject: %s - Message: %s - Date: %s", sender, recipients, subject, message, date);
    }
}
