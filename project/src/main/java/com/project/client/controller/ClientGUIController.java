package com.project.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    public TreeView<String> treeViewEmails;
    @FXML
    public WebView webViewEmail;

    public void initialize() {
        System.out.println("ClientGUIController initialized");
    }
}
