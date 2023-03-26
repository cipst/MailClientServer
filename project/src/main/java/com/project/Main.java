package com.project;

import com.project.client.ClientGUI;
import com.project.server.ServerView;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("MainGUI.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Startup Screen");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Main start...");
            launch(args);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
