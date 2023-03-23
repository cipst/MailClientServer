package com.project.server.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import com.project.server.LogHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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

    public void initialize() {
        File emailFile = new File(LogHandler.getLogsPath()+"/");
        File[] files = emailFile.listFiles();

        assert files != null;
        for (File file : files)
            logsListView.getItems().add(file.getName());

        logsListView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::readLogs);
    }

    private void readLogs(MouseEvent event){
        try {
            if (event.getClickCount() == 1) {
                String selected = logsListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    Scanner in = new Scanner(new FileReader(LogHandler.getLogsPath() + "/" + selected));
                    StringBuilder sb = new StringBuilder();
                    while (in.hasNextLine()) {
                        sb.append(in.nextLine());
                        sb.append(System.lineSeparator());
                    }
                    logsTextArea.setText(sb.toString());
                    in.close();
                }
            }
        } catch (Exception e) {
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
                labelStartStopServer.setText("Stop Server");
                String fileName = LogHandler.startServer();
                logsListView.getItems().add(fileName+".txt");
            }else{
                labelStartStopServer.setText("Start Server");
                String fileName = LogHandler.stopServer("stopped");
                System.out.println("fileName: " + fileName);
                logsListView.getItems().remove(logsListView.getItems().size() - 1);
                logsListView.getItems().add(fileName);

            }
        }catch(Exception e){
            System.out.println("ERROR: " + e);
        }
    }

    public void updateLogTextArea(String log){
        logsTextArea.appendText(log + "\n");
    }
}
