package com.project.client.controller;

import com.project.client.model.User;
import com.project.models.Email;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;


/**
 * User controller exists in order to have a single instance of user that is globally accessible.
 * This instance is created upon login and never changed
 *
 * @see com.project.client.controller.LoginGUIController login()
 */
public class UserController {
    private static User user;
    private static ListProperty<Email> emailsInbox = new SimpleListProperty<>();
    private static ObservableList<Email> emailsInboxContent = FXCollections.observableArrayList();
    private static ArrayList<Email> cache = new ArrayList<>();

    public static ListProperty<Email> emailsInboxProperty() {
        return emailsInbox;
    }


    static {
        emailsInbox.set(emailsInboxContent);
    }

    public static User getUser() {
        return user;
    }
    public static void setUser(User user) {
        UserController.user = user;
    }

    public static ArrayList<Email> getCache() {
        return cache;
    }

    public static void setCache(ArrayList<Email> cache) {
        UserController.cache = cache;
    }
}
