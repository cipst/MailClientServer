package com.project.server;

import com.project.server.controller.ConnectionController;
import com.project.server.controller.LogController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerGUI extends Application {

//    Database db = new Database();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ServerGUI.class.getResource("ServerGUI.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("Server opened");

        primaryStage.setOnCloseRequest(event -> {
            // INSERT HERE CODE TO CLOSE THE SERVER
            System.out.println("Server closed");
            if(ConnectionController.isServerOn())
                LogController.stopServer("closed");
        });
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
