package com.project.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Model class for an email
 */
public class Email implements java.io.Serializable {

    private final String sender;
    private final ArrayList<String> recipients;
    private final String subject;
    private final String message;

    private final Date date;

    public Email(String sender, ArrayList<String> recipients, String subject, String message, Date date) {
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.message = message;
        this.date = date;
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
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    @Override
    public String toString() {
        return String.format("Sender: %s - Recipients: %s - Subject: %s - Message: %s - Date: %s", sender, recipients, subject, message, date);
    }
}
