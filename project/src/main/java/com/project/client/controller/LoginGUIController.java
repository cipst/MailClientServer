package com.project.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginGUIController {

    @FXML
    public Button btnLogin;
    @FXML
    public TextField emailAddressField;
    @FXML
    public TextField passwordField;

    @FXML
    private void initialize() {
        System.out.println("LoginGUIController initialized");
    }

    @FXML
    private void login(){
        ConnectionController.startConnection();
    }
}
