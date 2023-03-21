package com.project;

import com.project.client.ClientMain;
import com.project.server.ServerMain;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("MainGUI.fxml"));
        Scene scene = new Scene(loader.load());

        EventHandler<ActionEvent> launchServer = event -> {
            try {
                Application server = ServerMain.class.getDeclaredConstructor().newInstance();
                Stage s2 = new Stage();
                server.start(s2);
                s2.setOnCloseRequest(e -> ((Button)event.getSource()).setDisable(false));
                ((Button)event.getSource()).setDisable(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        // Get the button from the FXML file and add the event handler
        Button btn = (Button) scene.lookup("#btnLaunchServer");
        btn.setOnAction(launchServer);

        // Get the button from the FXML file and add the event handler
        Button btnLaunchClientStefano = (Button) scene.lookup("#btnLaunchClientStefano");
        btnLaunchClientStefano.setOnAction(launchClientAction("stefano.cipolletta@unito.it", "stefano"));

        Button btnLaunchClientMatteo = (Button) scene.lookup("#btnLaunchClientMatteo");
        btnLaunchClientMatteo.setOnAction(launchClientAction("matteo.barone@unito.it", "matteo"));

        primaryStage.setTitle("Main - La Siummia");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private EventHandler<ActionEvent> launchClientAction(String clientEmail, String clientPsw) {
        return event -> {
            try{
                Application server = ClientMain.class.getDeclaredConstructor(String.class, String.class).newInstance(clientEmail, clientPsw);
                Stage s2 = new Stage();
                server.start(s2);
                s2.setOnCloseRequest(e -> ((Button)event.getSource()).setDisable(false));
                ((Button)event.getSource()).setDisable(true);
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        };
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
