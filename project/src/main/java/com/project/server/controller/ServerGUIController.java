package com.project.server.controller;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import com.project.models.Email;
import com.project.server.Database;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.controlsfx.control.ToggleSwitch;

public class ServerGUIController {
    @FXML
    public ToggleSwitch btnStartStopServer;
    @FXML
    public Text labelStartStopServer;
    @FXML
    public ListView<String> logsListView;
    @FXML
    public TextArea logsTextArea;

    private Thread thread;

    private ConnectionController connectionController = null;

    public void initialize() {
        connectionController = new ConnectionController(new Database());

        File emailFile = new File(LogController.getLogsPath()+"/");
        File[] files = emailFile.listFiles();

        assert files != null;
        for (File file : files)
            logsListView.getItems().add(file.getName());

        thread = new Thread(this::readLogs);
        thread.start();
    }

    public void readLogs(){
        try{
            while (true) {
                String selected = logsListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    Scanner in = new Scanner(new FileReader(LogController.getLogsPath() + "/" + selected));
                    StringBuilder sb = new StringBuilder();
                    while (in.hasNextLine()) {
                        sb.append(in.nextLine());
                        sb.append(System.lineSeparator());
                    }
                    Platform.runLater(() -> logsTextArea.setText(sb.toString()));
                    in.close();
                }

                // Sleep for some time before updating again
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is called by the ToggleSwitch when the user clicks on it.
     * It will create a new file with the current date and time when the server is started.
     * It will rename the file with the current date and time when the server is stopped.
     * The file will be saved in the logs folder.
     * The file name will be in the format: yyyyMMddTHHmmss-yyyyMMddTHHmmss.txt
     * Example: 20210101T000000-20210101T000001.txt
     */
    @FXML
    public void triggerToggle() {
        try{
            if(btnStartStopServer.isSelected()){

                connectionController.runServer();

                labelStartStopServer.setText("Stop Server");
                String fileName = LogController.startServer();
                logsListView.getItems().add(fileName+".txt");


//                Email e1 = new Email("stefano.cipolletta@unito.it", new ArrayList<String>() {{
//                    add("matteo.barone@unito.it");
//                }}, "Ciao", "Come stai?", "28/03/2023 16:39");
//
//                new Database().insertEmail(e1);
            }else{
                labelStartStopServer.setText("Start Server");
                String fileName = LogController.stopServer("stopped");
                System.out.println("fileName: " + fileName);
                logsListView.getItems().remove(logsListView.getItems().size() - 1);
                logsListView.getItems().add(fileName);

                connectionController.stopServer();
            }
            logsListView.getSelectionModel().selectLast();
        }catch(Exception e){
            System.out.println("ERROR: " + e);
        }
    }
}
