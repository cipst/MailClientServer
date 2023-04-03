package com.project.client.controller;

import com.project.client.ClientGUI;
import com.project.client.model.UserModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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
    private void login() {
        try {
            UserController.setUser(new UserModel(emailAddressField.getText(), passwordField.getText()));

            boolean success = ConnectionController.startConnection();
            if (success) {
                FXMLLoader loader = new FXMLLoader(ClientGUI.class.getResource("ClientGUI.fxml"));

                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) btnLogin.getScene().getWindow();

                stage.setTitle("Client");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
                System.out.println("Client GUI opened");

                stage.setOnCloseRequest(event -> System.out.println("Login closed"));
            }
        } catch (IOException e) {
            System.out.println("Error opening Client GUI");
        }
    }
}
