package com.project.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ServerMain.class.getResource("ServerGUI.fxml"));

        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Server - La Siummia");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        System.out.println("Server opened");

        primaryStage.setOnCloseRequest(event -> System.out.println("Server closed"));
    }



    public static void main(String[] args) {
        System.out.println("Init Server...");
        launch(args);
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
