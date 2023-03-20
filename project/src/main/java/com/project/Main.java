package com.project;

import com.project.server.ServerMain;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("MainGUI.fxml"));
        Scene scene = new Scene(loader.load(), 800, 580);

        EventHandler<ActionEvent> l = event -> {
            try {
                Application server = ServerMain.class.getDeclaredConstructor().newInstance();
                Stage s2 = new Stage();
                server.start(s2);
                //get server on stop
                s2.setOnCloseRequest(e -> {
                    System.out.println("CHIUSO");
                    ((Button)event.getSource()).setDisable(false);
                });
                ((Button)event.getSource()).setDisable(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        try {
            System.out.println("Main start...");
            launch(args);
        }catch(Exception e){
            System.out.println(e.getLocalizedMessage());
        }
    }
}
