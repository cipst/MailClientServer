package com.project;

import com.project.server.ServerMain;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Button btn_launch_server = new Button("Launch Server");
        Button btn_launch_server2 = new Button("Launch Server2");
        EventHandler<ActionEvent> l = event -> {
            try {
                Application server = ServerMain.class.getDeclaredConstructor().newInstance();
                Stage s2 = new Stage();
                server.start(s2);
                ((Button)event.getSource()).setDisable(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        btn_launch_server.setOnAction(l);
        btn_launch_server2.setOnAction(l);

        GridPane pane = new GridPane();
        pane.add(btn_launch_server, 0, 0);
        pane.add(btn_launch_server2, 0, 1);

        Scene s = new Scene(pane);
        primaryStage.setScene(s);
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
