package com.project.client.controller;

import com.project.models.Email;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;


public class ConnectionController {
    private static ListProperty<Email> emailsInbox = new SimpleListProperty<>();
    private static ObservableList<Email> emailsInboxContent = FXCollections.observableArrayList();

    static {
        emailsInbox.set(emailsInboxContent);
//        initConnection();
//        fillInbox();
    }

    public static ListProperty<Email> emailsInboxProperty() {
        return emailsInbox;
    }

    private static void initConnection(){
        try{
            new Alert(Alert.AlertType.INFORMATION, "Connection initialized").showAndWait();
            //TODO: connect to server
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void fillInbox(){
        try{
            //TODO: get emails from server

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void deleteEmail(Email email) {

        emailsInbox.remove(email);
        //TODO: delete email from server
        new Alert(Alert.AlertType.INFORMATION, "Email eliminated").showAndWait();
    }

    public static void sendEmail(Email email) {

        //TODO: send email to server
        new Alert(Alert.AlertType.INFORMATION, String.format("Email sended\nFrom:%s\nTo:%s\nSubject:%s\nMessage:%s\n", email.getSender(), email.getRecipients().get(0), email.getSubject(), email.getMessage())).showAndWait();
        emailsInboxContent.add(email);
    }
}
