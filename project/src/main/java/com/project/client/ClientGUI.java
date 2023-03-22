package com.project.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientMain extends Application {

    private final String email;
    private final String password;

    public ClientMain(String email, String password){
        System.out.println("Client email: " + email);
        System.out.println("Client password: " + password);
        this.email = email;
        this.password = password;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("ClientGUI.fxml"));

        Scene scene = new Scene(loader.load());
        primaryStage.setTitle(this.email);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        System.out.println("Client opened");

        primaryStage.setOnCloseRequest(event -> System.out.println("Client closed"));
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
