package com.project.server;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;

public class LogHandler {

    private static final String LOGS_PATH = "src/main/resources/com/project/server/logs";

    private static String filePath = "";

    private static void createFile(String fileName) {
        try{
            filePath = LOGS_PATH+"/"+fileName+".txt";
            File file = new File(filePath);

            if (file.createNewFile())
                System.out.println("File created: " + file.getName());
            else
                System.out.println("File already exists.");

        }catch(Exception e){
            System.out.println("ERROR: " + e);
        }
    }

    private static void write(String log) {
        try{
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(log + "\n");
            writer.close();
        }catch(Exception e){
            System.out.println("ERROR: " + e);
        }
    }

    public static void startServer(){
        LocalDateTime now = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        String fileName = now.toString().replace(":", "").replace("-", "");

        createFile(fileName);
        write(String.format("---- Server started at %s ----", LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS)));
    }

    public static void stopServer(String reason){
        LocalDateTime now = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        String fileName = now.toString().replace(":", "").replace("-", "");

        File oldFile = new File(filePath);
        filePath = String.format("%s-%s.txt", filePath.substring(0, filePath.length()-4), fileName);

        File newFile = new File(filePath);

        if(oldFile.renameTo(newFile)) {
            System.out.println("File renamed to " + filePath);
        } else {
            System.out.println("Failed to rename file");
        }
        write(String.format("---- Server %s at %s ----", reason, LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS)));
    }
}
