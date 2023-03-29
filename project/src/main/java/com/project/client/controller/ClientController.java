package com.project.client.controller;

import com.project.models.Email;
import javafx.collections.ObservableList;

public class ClientController {

    //TODO: DO THE SAME WHIT THE OUTBOX
    private static ObservableList<Email> emailsInbox;

    public static void deleteEmail(Email email) {
        emailsInbox.remove(email);
        //TODO: delete email from server
    }
}
