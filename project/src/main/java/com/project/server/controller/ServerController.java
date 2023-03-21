package com.project.server.controller;

import java.io.File;
import java.time.LocalDateTime;

import com.project.server.LogHandler;
import javafx.fxml.FXML;
import org.controlsfx.control.ToggleSwitch;

public class ServerController {

    private final String LOGS_PATH = "src/main/resources/com/project/server/logs";

    private static String filePath = "";

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
            LocalDateTime now = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
            String fileName = now.toString().replace(":", "").replace("-", "");

            if(btnStartStopServer.isSelected()){
                filePath = LOGS_PATH+"/"+fileName+".txt";
                File file = new File(filePath);

                if (file.createNewFile())
                    System.out.println("File created: " + file.getName());
                else
                    System.out.println("File already exists.");

                LogHandler.startServer();
            }else{
                File oldFile = new File(filePath);
                filePath = String.format("%s-%s.txt", filePath.substring(0, filePath.length()-4), fileName);

                File newFile = new File(filePath);

                if(oldFile.renameTo(newFile)) {
                    System.out.println("File renamed successfully");
                } else {
                    System.out.println("Failed to rename file");
                }

                LogHandler.stopServer("stopped");
            }
        }catch(Exception e){
            System.out.println("ERROR: " + e);
        }
    }

    public static String getFilePath() {
        return filePath;
    }

}
