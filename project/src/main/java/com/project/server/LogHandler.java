package com.project.server;

import com.project.server.controller.ServerController;

import java.io.FileWriter;
import java.time.LocalDateTime;

public class LogHandler {

    private static void write(String log) {
        try{
            FileWriter writer = new FileWriter(ServerController.getFilePath(), true);
            writer.write(log + "\n");
            writer.close();
        }catch(Exception e){
            System.out.println("ERROR: " + e);
        }
    }

    public static void startServer(){
        write(String.format("---- Server started at %s ----", LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS)));
    }

    public static void stopServer(String reason){
        write(String.format("---- Server %s at %s ----", reason, LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS)));
    }
}
