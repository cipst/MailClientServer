package com.project;

import com.project.client.ClientGUI;
import com.project.server.ServerView;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {
    @FXML
    public Button btnLaunchServer;
    @FXML
    public Button btnLaunchClientStefano;
    @FXML
    public Button btnLaunchClientMatteo;


    public void initialize() {

        // Get the button from the FXML file and add the event handler
        btnLaunchServer.setOnAction(launchServer);

        // Get the button from the FXML file and add the event handler
        btnLaunchClientStefano.setOnAction(launchClientAction("stefano.cipolletta@unito.it", "stefano"));
        btnLaunchClientMatteo.setOnAction(launchClientAction("matteo.barone@unito.it", "matteo"));
    }

    private EventHandler<ActionEvent> launchServer = event -> {
        try {
            Application server = ServerView.class.getDeclaredConstructor().newInstance();
            Stage server_stage = new Stage();
            server.start(server_stage);
            ((Button) event.getSource()).setDisable(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    private EventHandler<ActionEvent> launchClientAction(String clientEmail, String clientPsw) {
        return event -> {
            try {
                Application server = ClientGUI.class.getDeclaredConstructor(String.class, String.class).newInstance(clientEmail, clientPsw);
                Stage client_stage = new Stage();
                server.start(client_stage);
                client_stage.setOnCloseRequest(e -> ((Button) event.getSource()).setDisable(false));
                ((Button) event.getSource()).setDisable(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
