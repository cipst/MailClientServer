package com.project.server.controller;

import java.io.File;
import java.time.LocalDateTime;

import com.project.server.LogHandler;
import javafx.fxml.FXML;
import org.controlsfx.control.ToggleSwitch;

public class ServerGUIController {

    @FXML
    public ToggleSwitch btnStartStopServer;

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
                LogHandler.startServer();
            }else{
                LogHandler.stopServer("stopped");
            }
        }catch(Exception e){
            System.out.println("ERROR: " + e);
        }
    }
}
