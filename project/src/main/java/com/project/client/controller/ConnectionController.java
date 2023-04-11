package com.project.client.controller;

import com.project.client.model.User;
import com.project.models.ConnectionRequest;
import com.project.models.EmailRequest;
import com.project.models.Email;
import com.project.models.Response;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ConnectionController {

    private static final int CONNECTION_PORT = 1234;
    private static ObjectProperty<Color> serverStatus = new SimpleObjectProperty<>(Color.LAWNGREEN);
    private static BooleanProperty actionsDisabled = new SimpleBooleanProperty(true);

    private static Socket socket;
    private static ObjectOutputStream outStream;
    private static ObjectInputStream inStream;

    private static boolean isServerOn = true;

    public static ObjectProperty<Color> serverStatusProperty() {
        return serverStatus;
    }

    private static void initConnectionObjects() throws IOException {
        socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
        outStream = new ObjectOutputStream(socket.getOutputStream());

        socket.setSoTimeout(1000);
        inStream = new ObjectInputStream(socket.getInputStream());
    }

    private static void closingConnectionObjects(String from) {
        try {
            outStream.close();
            inStream.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(from + e.getMessage());
        }
    }

    /**
     * It starts a new Executor Scheduled Service that either fills the inbox or tries to reconnect to the server
     * depending on the server status
     */
    public static void startExecutorService() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            if (isServerOn) {
                Platform.runLater(() ->
                {
                    try {
                        if (ConnectionController.fillInbox()) {
                            Platform.runLater(() -> {
                                new Alert(Alert.AlertType.INFORMATION, "You received a new email!\nCheck it up!").showAndWait();
                            });
                        }
                    } catch (Exception e) {
                        System.out.println("[startServiceThread] Server is down: " + e.getMessage());
                        changeServerStatus(false);
                    }
                });
            } else {
                try {
                    ConnectionController.startConnection();
                    System.out.println("Server is up thanks to startConnection call");
                    changeServerStatus(true);

                    sendCachedEmails();

                    ClientGUIController.setSelectedEmail(null);
                } catch (Exception e) {
                    System.out.println("[startServiceThread] Server is still down: " + e.getMessage());
                }
            }
        }, 2, 1, TimeUnit.SECONDS);
    }

    private static void sendCachedEmails() {
        ArrayList<Email> cachedEmails = UserController.getCache();
        if (cachedEmails.size() > 0) {
            for (Email email : cachedEmails) {
                try {
                    ConnectionController.sendEmail(email);
                } catch (Exception e) {
                    System.out.println("[sendCachedEmails] " + e.getMessage());
                }
            }
            UserController.setCache(new ArrayList<>());
        }
    }

    private static void changeServerStatus(boolean newStatus) {
        if (newStatus) {
            serverStatus.setValue(Color.LAWNGREEN);
            isServerOn = true;
        } else {
            serverStatus.setValue(Color.RED);
            isServerOn = false;
        }
    }

    /**
     * It starts the connection to the server
     * It sends a ConnectionRequest to the server and waits for a Response
     * If the Response is successful it fills the inbox with the emails received from the server
     *
     * @throws Exception
     */
    public static void startConnection() throws Exception {

        User user = UserController.getUser();

        try {
            initConnectionObjects();

            ConnectionRequest conn = new ConnectionRequest(user.getAddress(), user.getPassword(), ConnectionRequest.Status.CONNECT);
            outStream.writeObject(conn);

            Object obj = inStream.readObject();
            if (!(obj instanceof Response res)) throw new Exception("Invalid Response");

            if (!res.isSuccessful())
                throw new Exception(res.getMessage());

            UserController.emailsInboxProperty().clear();
            UserController.emailsInboxProperty().addAll(((ArrayList<Email>) res.getData()));
            UserController.emailsInboxProperty().sort(Email::compareTo);
            setActionsDisabled(true);
        } catch (IOException e) {
            System.out.println("[startConnection] Connection Error: " + e.getMessage());
            throw new Exception("Connection Error");
        } finally {
            closingConnectionObjects("[startConnection] Error on closing: ");
        }
    }

    /**
     * it ends the connection with the server
     * It sends a ConnectionRequest to the server and waits for a Response
     *
     * @throws Exception
     */
    public static void endConnection() throws Exception {
        if (!isServerOn) throw new Exception("Server is down");

        try {
            initConnectionObjects();

            ConnectionRequest conn = new ConnectionRequest(UserController.getUser().getAddress(), UserController.getUser().getPassword(), ConnectionRequest.Status.DISCONNECT);
            outStream.writeObject(conn);

            Object obj = inStream.readObject();
            if (!(obj instanceof Response res)) throw new Exception("Invalid Response");

            if (!res.isSuccessful())
                throw new Exception("Invalid Credentials");
        } catch (IOException e) {
            System.out.println("[endConnection] Connection Error: " + e.getMessage());
            throw new Exception("Connection Error");
        } finally {
            closingConnectionObjects("[endConnection] Error on closing: ");
        }
    }

    /**
     * It sends an EmailRequest to the server to fill the inbox
     * It waits for a Response from the server
     * If the Response is successful it fills the inbox with the emails received from the server
     *
     * @return true if the inbox has been filled with at least an email, false otherwise
     * @throws Exception
     */
    public static boolean fillInbox() throws Exception {
        if (!isServerOn) throw new Exception("Server is down");

        User user = UserController.getUser();

        try {
            initConnectionObjects();

            EmailRequest req = new EmailRequest(user.getAddress(), EmailRequest.RequestType.FILL_INBOX);
            outStream.writeObject(req);

            Object obj = inStream.readObject();
            if (!(obj instanceof Response res)) throw new Exception("Invalid Response");

            if (!res.isSuccessful()) throw new Exception(res.getMessage());

            ArrayList<Email> emails = (ArrayList<Email>) res.getData();
            if (emails.size() > 0) {
                UserController.emailsInboxProperty().addAll(0, emails);
                UserController.emailsInboxProperty().sort(Email::compareTo);
                return true;
            }

            return false;
        } catch (IOException e) {
            System.out.println("[fillInbox] Connection Error: " + e.getMessage());
            throw new Exception("Connection Error");
        } finally {
            closingConnectionObjects("[fillInbox] Error on closing: ");
        }
    }

    /**
     * It sends an EmailRequest to the server to delete an email from the inbox
     * It waits for a Response from the server
     * If the Response is successful it deletes the email from the inbox
     *
     * @param email the email to delete
     * @throws Exception when the server wasn't successful in performing the operation
     */
    public static void deleteEmail(Email email) throws Exception {
        if (!isServerOn) throw new Exception("Server is down");

        User user = UserController.getUser();
        try {
            initConnectionObjects();

            EmailRequest conn = new EmailRequest(user.getAddress(), EmailRequest.RequestType.DELETE_FROM_INBOX, email);
            outStream.writeObject(conn);

            Object obj = inStream.readObject();
            if (!(obj instanceof Response res)) throw new Exception("Invalid Response");

            if (!res.isSuccessful())
                throw new Exception(res.getMessage());

            UserController.emailsInboxProperty().remove(email);
        } catch (IOException e) {
            System.out.println("[deleteEmail] Connection Error: " + e.getMessage());
            throw new Exception("Connection Error");
        } finally {
            closingConnectionObjects("[deleteEmail] Error on closing: ");
        }
    }

    /**
     * It sends an EmailRequest to the server to send an email
     * It waits for a Response from the server
     * If the Response is successful the email was sent correctly
     *
     * @param email the email to send
     * @throws Exception when the server wasn't successful in performing the operation
     */
    public static void sendEmail(Email email) throws Exception {
        if (!isServerOn) {
            UserController.getCache().add(email);
            throw new Exception("Server is down.\nThe email has been saved and will be sent when the server comes up again don't close the client!");
        }


        User user = UserController.getUser();

        try {
            initConnectionObjects();

            EmailRequest conn = new EmailRequest(user.getAddress(), EmailRequest.RequestType.SEND, email);
            outStream.writeObject(conn);

            Object obj = inStream.readObject();
            if (!(obj instanceof Response res)) throw new Exception("Invalid Response");

            if (!res.isSuccessful()) {
                throw new Exception("Wrong Recipients:\n" + res.getData().toString());
            }
        } catch (IOException e) {
            System.out.println("[sendEmail] Connection Error: " + e.getMessage());
            throw new Exception("Connection Error");
        } finally {
            closingConnectionObjects("[sendEmail] Error on closing: ");
        }
    }


    public static BooleanProperty actionsDisabledProperty() {
        return actionsDisabled;
    }

    public static void setActionsDisabled(boolean actionsDisabled) {
        ConnectionController.actionsDisabled.set(actionsDisabled);
    }
}
