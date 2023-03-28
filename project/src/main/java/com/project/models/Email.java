package com.project.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Model class for an email
 */
public class Email {

    private int id;
    private StringProperty sender;
    private ObservableList<String> recipients;
    private StringProperty subject;
    private StringProperty message;
    private StringProperty date;

    public Email(int id, String sender, ArrayList<String> recipients, String subject, String message, String date) {
        this.id = id;
        this.sender = new SimpleStringProperty(sender);
        this.recipients = FXCollections.observableArrayList(recipients);
        this.subject = new SimpleStringProperty(subject);
        this.message = new SimpleStringProperty(message);
        this.date = new SimpleStringProperty(date);
    }

    public Email(String sender, ArrayList<String> recipients, String subject, String message, String date) {
        this.id = -1;
        this.sender = new SimpleStringProperty(sender);
        this.recipients = FXCollections.observableArrayList(recipients);
        this.subject = new SimpleStringProperty(subject);
        this.message = new SimpleStringProperty(message);
        this.date = new SimpleStringProperty(date);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender.get();
    }

    public StringProperty senderProperty() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender.set(sender);
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients.setAll(recipients);
    }

    public String getSubject() {
        return subject.get();
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }
}
