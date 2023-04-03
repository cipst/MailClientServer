package com.project.client.model;

import com.project.models.EmailSerializable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class ClientModel {
    private StringProperty email;
    private ObservableList<EmailSerializable> listOfMessages;
    private EmailSerializable selectedMail;

    public ClientModel(String email) {
        this.email = new SimpleStringProperty();
        this.email.setValue(email);
        listOfMessages = FXCollections.observableArrayList();
        selectedMail = new EmailSerializable(-1, "", new ArrayList<>(), "", "", "");
    }

    public StringProperty getEmail() {
        return email;
    }

    public ObservableList<EmailSerializable> getListOfMessages() {
        return listOfMessages;
    }

    public EmailSerializable getSelectedMail() {
        return selectedMail;
    }

    /**
     * It sets the selectedMail to an empty representation
     */
    public void setSelectedMailEmpty() {
        selectedMail.setId(-1);
        selectedMail.setSender("");
        selectedMail.setRecipients(new ArrayList<String>());
        selectedMail.setSubject("");
        selectedMail.setMessage("");
        selectedMail.setDate("");
    }

    /**
     * It sets the selectedMail property to the current selected mail values
     *
     * @param newSelection is the mail currently selected
     */
    public void setSelectedMail(EmailSerializable newSelection) {
        selectedMail.setId(newSelection.getId());
        selectedMail.setSender(newSelection.getSender());
        selectedMail.setRecipients(newSelection.getRecipients());
        selectedMail.setSubject(newSelection.getSubject());
        selectedMail.setMessage(newSelection.getMessage());
        selectedMail.setDate(newSelection.getDate());
    }

    /**
     * It converts the received MailSerializable into Mail and adds it to the Client list of Mails
     *
     * @param newMail is the new MailSerializable received
     */
    public void addNewMail(EmailSerializable newMail) {
        listOfMessages.add(0, newMail);
    }

}

