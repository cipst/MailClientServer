package com.project.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;

public class ClientGUIController {
    @FXML
    public Button btnReply;
    @FXML
    public Button btnReplyAll;
    @FXML
    public Button btnForward;
    @FXML
    public Button btnDelete;
    @FXML
    public Button btnNewEmail;
    @FXML
    public TreeView<String> treeViewEmailsInbox;
    @FXML
    public TreeView<String> treeViewEmailsOutbox;
    @FXML
    public WebView webViewEmail;

    public void initialize() {
        System.out.println("ClientGUIController initialized");

        //Select all SplitPane dividers and set their position to 0.5


        webViewEmail.getEngine().loadContent("<h1>Test</h1><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><h1>CIAO</h1>");

        treeViewEmailsInbox.setRoot(new TreeItem<>("Inbox"));
        treeViewEmailsOutbox.setRoot(new TreeItem<>("Outbox"));

//        treeViewEmailsInbox.getRoot().getChildren().add(new TreeItem<>("Email 1"));

    }

    @FXML
    public void newEmail(){
        System.out.println("btnNewEmail clicked");
    }

    @FXML
    public void reply() {
        System.out.println("btnReply clicked");
    }

    @FXML
    public void replyAll() {
        System.out.println("btnReplyAll clicked");
    }

    @FXML
    public void forward() {
        System.out.println("btnForward clicked");
    }

    @FXML
    public void delete() {
        System.out.println("btnDelete clicked");
    }
}
