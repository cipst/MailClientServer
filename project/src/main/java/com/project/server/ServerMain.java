package com.project.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ServerMain.class.getResource("Server.fxml"));

        Scene scene = new Scene(loader.load(), 800, 580);
        primaryStage.setTitle("Server - La Siummia");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.out.println("CHIUSO"));
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
