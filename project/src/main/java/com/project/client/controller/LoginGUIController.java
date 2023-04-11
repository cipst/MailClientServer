package com.project.client.controller;

import com.project.client.ClientGUI;
import com.project.client.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginGUIController {

    @FXML
    public Button btnLogin;
    @FXML
    public TextField emailAddressField;
    @FXML
    public TextField passwordField;

    @FXML
    /**
     * This method is called when the user clicks on the login button.
     * It will check if the user has filled in all the fields.
     */
    private void login() {
        try {
            if (emailAddressField.getText().equals("") || passwordField.getText().equals("")) {
                new Alert(Alert.AlertType.ERROR, "Please fill in all fields").showAndWait();
                return;
            }

            // Set the GLOBAL USER
            UserController.setUser(new User(emailAddressField.getText(), passwordField.getText()));

            ConnectionController.startConnection();

            FXMLLoader loader = new FXMLLoader(ClientGUI.class.getResource("ClientGUI.fxml"));

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnLogin.getScene().getWindow();

            stage.setTitle("Client");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            stage.setOnCloseRequest(event -> {
                try {
                    ConnectionController.endConnection();
                    System.exit(0);
                } catch (Exception e) {
                    System.out.println("Error closing connection");
                    System.exit(0);
                }
            });
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            System.out.println("Error opening Client GUI");
        }
    }
}
