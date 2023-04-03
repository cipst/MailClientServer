package com.project.server.controller;

import com.project.server.Database;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import org.controlsfx.control.ToggleSwitch;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

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

        File emailFile = new File(LogController.getLogsPath() + "/");
        File[] files = emailFile.listFiles();

        assert files != null;
        for (File file : files)
            logsListView.getItems().add(file.getName());

        logsTextArea.textProperty().bind(LogController.currentMessagesLogProperty());

        logsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                System.out.println("Selected item: " + newValue);
                String selected = logsListView.getSelectionModel().getSelectedItem();
                readLog(selected);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    public void readLog(String selected) {
        try {
            if (selected != null) {
                Scanner in = new Scanner(new FileReader(LogController.getLogsPath() + "/" + selected));
                StringBuilder sb = new StringBuilder();
                while (in.hasNextLine()) {
                    sb.append(in.nextLine());
                    sb.append(System.lineSeparator());
                }
                LogController.setCurrentMessagesLog(sb.toString());
                in.close();
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
        try {
            if (btnStartStopServer.isSelected()) {

                connectionController.runServer();

                labelStartStopServer.setText("Stop Server");
                String fileName = LogController.startServer();

                System.out.println("FILE NAME: " + fileName);

                logsListView.getItems().add(fileName + ".txt");


//                Email e1 = new Email("stefano.cipolletta@unito.it", new ArrayList<String>() {{
//                    add("matteo.barone@unito.it");
//                }}, "Ciao", "Come stai?", "28/03/2023 16:39");
//
//                new Database().insertEmail(e1);
            } else {
                labelStartStopServer.setText("Start Server");
                String fileName = LogController.stopServer("stopped");
                System.out.println("fileName: " + fileName);
                logsListView.getItems().remove(logsListView.getItems().size() - 1);
                logsListView.getItems().add(fileName);

                connectionController.stopServer();
            }
            logsListView.getSelectionModel().selectLast();
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            e.printStackTrace();
        }
    }
}
