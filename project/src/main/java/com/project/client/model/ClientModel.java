package com.project.client.model;

import java.util.ArrayList;

import com.project.models.Email;
import com.project.models.EmailConverter;
import com.project.models.EmailSerializable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientModel {
    private StringProperty email;
    private ObservableList<Email> listOfMessages;
    private Email selectedMail;

    public ClientModel(String email) {
        this.email = new SimpleStringProperty();
        this.email.setValue(email);
        listOfMessages = FXCollections.observableArrayList();
        selectedMail = new Email("", new ArrayList<>(), "", "", "");
    }

    public StringProperty getEmail() {
        return email;
    }

    public ObservableList<Email> getListOfMessages() {
        return listOfMessages;
    }

    public Email getSelectedMail() {
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
    public void setSelectedMail(Email newSelection) {
        selectedMail.setId(newSelection.getId());
        selectedMail.setSender(newSelection.getSender());
        selectedMail.setRecipients(newSelection.getRecipients());
        selectedMail.setSubject(newSelection.getSubject());
        selectedMail.setMessage(newSelection.getMessage());
        selectedMail.setDate(newSelection.getDate());
    }

    /**
     * Adds a new received Mail from the Server to the Client list of Mails
     *
     * @param newMail is the new Mail received
     */
    public void addNewMail(Email newMail) {
        listOfMessages.add(newMail);
    }

    /**
     * It converts the received MailSerializable into Mail and adds it to the Client list of Mails
     *
     * @param newMail is the new MailSerializable received
     */
    public void addNewMail(EmailSerializable newMail) {
        listOfMessages.add(0, EmailConverter.toMail(newMail));
    }

}

