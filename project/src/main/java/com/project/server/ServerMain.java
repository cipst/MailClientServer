package com.project.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class ServerMain extends Application {

    Database db = new Database();

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ServerMain.class.getResource("ServerGUI.fxml"));

        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        System.out.println("Server opened");

        primaryStage.setOnCloseRequest(event -> {
            // INSERT HERE CODE TO CLOSE THE SERVER
            System.out.println("Server closed");

            LogHandler.stopServer("closed");
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
