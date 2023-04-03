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

    public EmailSerializable(String sender, ArrayList<String> recipients, String subject, String message, String date) {
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

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setRecipients(ArrayList<String> recipients) {
        this.recipients = recipients;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("Sender: %s - Recipients: %s - Subject: %s - Message: %s - Date: %s", sender, recipients, subject, message, date);
    }
}
