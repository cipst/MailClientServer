package com.project.server.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

public class LogController {

    private static final String LOGS_PATH = "src/main/resources/com/project/server/logs";

    private static String filePath = "";

    private static StringProperty currentMessagesLog = new SimpleStringProperty();

    public static void setCurrentMessagesLog(String currentMessagesLog) {
        LogController.currentMessagesLog.setValue(currentMessagesLog);
    }

    public static String getCurrentMessagesLog() {
        return currentMessagesLog.get();
    }

    public static StringProperty currentMessagesLogProperty() {
        return currentMessagesLog;
    }

    public static String getLogsPath() {
        return LOGS_PATH;
    }

    private static void createFile(String fileName) {
        try {
            filePath = LOGS_PATH + "/" + fileName + ".txt";
            File file = new File(filePath);

            if (file.createNewFile())
                System.out.println("File created: " + file.getName());
            else
                System.out.println("File already exists.");

        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
    }

    private static void write(String log) {
        try {
            FileWriter writer = new FileWriter(filePath, true);
            Date d = new Date();
            String day = DateFormat.getDateInstance(DateFormat.SHORT).format(d);
            String time = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(d);
            String message = String.format("[%s] [%s]: %s\n", day, time, log);
            currentMessagesLog.setValue(currentMessagesLog.getValueSafe() + message);
            writer.write(message);
            writer.close();
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
    }

    private static String getFileName() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString().replace(":", "").replace("-", "");
    }

    public static String startServer() {
        String fileName = getFileName();

        createFile(fileName);
        write("Server started");
        return fileName;
    }

    public static String stopServer(String reason) {
        assert !filePath.equals("");
        String fileName = getFileName();
        File oldFile = new File(filePath);

        filePath = String.format("%s-%s.txt", filePath.substring(0, filePath.length() - 4), fileName);
        File newFile = new File(filePath);

        if (oldFile.renameTo(newFile)) {
            System.out.println("File renamed to " + newFile.getName());
        } else {
            System.out.println("Failed to rename file");
        }

        write(String.format("Server %s", reason));
        return newFile.getName();
    }

    public static void emailSent(String by, ArrayList<String> to) {
        assert !filePath.equals("");
        write(String.format("Email sent by %s to %s", by, to));
    }

    public static void emailReceived(String from) {
        assert !filePath.equals("");
        write(String.format("Email received from %s", from));
    }

    public static void loginRequest(String address) {
        assert !filePath.equals("");
        write(String.format("%s is trying to login", address));
    }

    public static void clientDisconnected(String address) {
        assert !filePath.equals("");
        write(String.format("%s has disconnected", address));
    }

    public static void loginAccepted(String address) {
        assert !filePath.equals("");
        write(String.format("%s has successfully logged in", address));
    }

    public static void loginDenied(String address, String reason) {
        assert !filePath.equals("");
        write(String.format("%s: %s", reason, address));
    }

    public static void emailRejected(String sender, ArrayList<String> wrongRecipients) {
        assert !filePath.equals("");
        write(String.format("Email from %s rejected because of wrong recipients: %s", sender, wrongRecipients));
    }
}
