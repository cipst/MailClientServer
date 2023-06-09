package com.project.server.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is used to create a log file with the current date and time when the server is started.
 * It will rename the file with the current date and time when the server is stopped.
 * The file will be saved in the logs folder.
 * The file name will be in the format: yyyyMMddTHHmmss-yyyyMMddTHHmmss.txt
 * It contains a method for each operation that the server performs.
 */
public class LogController {

    private static final String LOGS_PATH = "src/main/resources/com/project/server/logs";
    private static String filePath = "";
    private static StringProperty currentMessagesLog = new SimpleStringProperty();
    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public static void setCurrentMessagesLog(String currentMessagesLog) {
        LogController.currentMessagesLog.setValue(currentMessagesLog);
    }

    public static StringProperty currentMessagesLogProperty() {
        return currentMessagesLog;
    }

    public static String getLogsPath() {
        return LOGS_PATH;
    }

    private static void createFile(String fileName) {
        try {
            File dir = new File(LOGS_PATH);
            dir.mkdirs();

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

    public static void read(String selected) {
        try {
            if (selected != null) {
                Scanner in = new Scanner(new FileReader(getLogsPath() + "/" + selected));
                StringBuilder sb = new StringBuilder();

                lock.readLock().lock();
                while (in.hasNextLine()) {
                    sb.append(in.nextLine());
                    sb.append(System.lineSeparator());
                }
                lock.readLock().unlock();

                Platform.runLater(() -> {
                    setCurrentMessagesLog(sb.toString());
                });

                in.close();
            }
        } catch (Exception e) {
            System.out.println("[LogController] [read] Error: " + e.getMessage());
        } finally {
            if (lock.getReadLockCount() > 0)
                lock.readLock().unlock();
        }
    }

    private static void write(String log) {
        FileWriter writer = null;
        try {
            lock.writeLock().lock();
            writer = new FileWriter(filePath, true);
            Date d = new Date();
            String day = DateFormat.getDateInstance(DateFormat.SHORT).format(d);
            String time = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(d);
            String message = String.format("[%s] [%s]: %s\n", day, time, log);
            Platform.runLater(() -> currentMessagesLog.setValue(currentMessagesLog.getValueSafe() + message));
            writer.write(message);
        } catch (Exception e) {
            System.out.println("[LogController] [write] Error: " + e.getMessage());
        } finally {
            try {
                if(writer != null)
                    writer.close();
            } catch (Exception e) {
                System.out.println("[LogController] [write] Error: " + e.getMessage());
            } finally {
                lock.writeLock().unlock();
            }
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
        if(filePath.equals("")) return "";

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
        if (filePath.equals("")) return;
        write(String.format("Email sent by %s to %s", by, to));
    }

    public static void emailReceived(String from) {
        if (filePath.equals("")) return;
        write(String.format("Email received from %s", from));
    }

    public static void loginRequest(String address) {
        if (filePath.equals("")) return;
        write(String.format("%s is trying to login", address));
    }

    public static void clientDisconnected(String address) {
        if (filePath.equals("")) return;
        write(String.format("%s has disconnected", address));
    }

    public static void loginAccepted(String address) {
        if (filePath.equals("")) return;
        write(String.format("%s has successfully logged in", address));
    }

    public static void loginDenied(String address, String reason) {
        if (filePath.equals("")) return;
        write(String.format("%s: %s", reason, address));
    }

    public static void emailRejected(String sender, ArrayList<String> wrongRecipients) {
        if (filePath.equals("")) return;
        write(String.format("Email from %s rejected because of wrong recipients: %s", sender, wrongRecipients));
    }

    public static void emailDeleted(String account) {
        if (filePath.equals("")) return;
        write(String.format("Email deleted from %s", account));
    }

    public static void failEmailDeleted(String account) {
        if (filePath.equals("")) return;
        write(String.format("Failed to delete email from %s", account));
    }
}
