package com.project.client.controller;

import com.project.client.ClientGUI;
import com.project.client.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginGUIController {

    @FXML
    public Button btnLogin;
    @FXML
    public TextField emailAddressField;
    @FXML
    public PasswordField passwordField;

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
                    if (!UserController.getCache().isEmpty()) {
                        Optional<ButtonType> choice = new Alert(Alert.AlertType.CONFIRMATION, "You have unsent emails. If you close the application you will lose them.\nAre you sure you want to exit?").showAndWait();
                        if (choice.isPresent() && choice.get() == ButtonType.OK) {
                            ConnectionController.endConnection();
                            System.exit(0);
                        } else {
                            event.consume();
                        }
                    } else {
                        ConnectionController.endConnection();
                        System.exit(0);
                    }
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
