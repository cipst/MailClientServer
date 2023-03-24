package com.project.server;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class LogHandler {

    private static final String LOGS_PATH = "src/main/resources/com/project/server/logs";

    private static String filePath = "";

    public static String getLogsPath() {
        return LOGS_PATH;
    }

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
            writer.write(String.format("%s ---- %s\n", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), log));
            writer.close();
        }catch(Exception e){
            System.out.println("ERROR: " + e);
        }
    }

    private static String getFileName(){
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString().replace(":", "").replace("-", "");
    }

    public static String startServer(){
        String fileName = getFileName();

        createFile(fileName);
        write("Server started");
        return fileName;
    }

    public static String stopServer(String reason){
        assert !filePath.equals("");
        String fileName = getFileName();
        File oldFile = new File(filePath);

        filePath = String.format("%s-%s.txt", filePath.substring(0, filePath.length()-4), fileName);
        File newFile = new File(filePath);

        if(oldFile.renameTo(newFile)) {
            System.out.println("File renamed to " + newFile.getName());
        } else {
            System.out.println("Failed to rename file");
        }

        write(String.format("Server %s", reason));
        return newFile.getName();
    }
}
