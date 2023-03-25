package com.project;

import com.project.client.ClientGUI;
import com.project.server.ServerView;
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
                Application server = ServerView.class.getDeclaredConstructor().newInstance();
                Stage server_stage = new Stage();
                server.start(server_stage);
//                server_stage.setOnCloseRequest(e -> {
//                    ((Button) event.getSource()).setDisable(false);
//                    System.out.println("Server closed");
//                });
//                ((Button)event.getSource()).setDisable(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        // Get the button from the FXML file and add the event handler
        Button btn_launch_server = (Button) scene.lookup("#btnLaunchServer");
        btn_launch_server.setOnAction(launchServer);

        // Get the button from the FXML file and add the event handler
        Button btn_launch_client_stefano = (Button) scene.lookup("#btnLaunchClientStefano");
        btn_launch_client_stefano.setOnAction(launchClientAction("stefano.cipolletta@unito.it", "stefano"));

        Button btn_launch_client_matteo = (Button) scene.lookup("#btnLaunchClientMatteo");
        btn_launch_client_matteo.setOnAction(launchClientAction("matteo.barone@unito.it", "matteo"));

        primaryStage.setTitle("Startup Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private EventHandler<ActionEvent> launchClientAction(String clientEmail, String clientPsw) {
        return event -> {
            try{
                Application server = ClientGUI.class.getDeclaredConstructor(String.class, String.class).newInstance(clientEmail, clientPsw);
                Stage client_stage = new Stage();
                server.start(client_stage);
                client_stage.setOnCloseRequest(e -> ((Button)event.getSource()).setDisable(false));
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
