package com.project.client.controller;

import com.project.client.model.UserModel;
import com.project.models.ConnectionRequest;
import com.project.models.EmailRequest;
import com.project.models.Email;
import com.project.models.Response;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private static ListProperty<Email> emailsInbox = new SimpleListProperty<>();
    private static ObservableList<Email> emailsInboxContent = FXCollections.observableArrayList();
    private static ObjectProperty<Color> serverStatus = new SimpleObjectProperty<>(Color.LAWNGREEN);
    private static BooleanProperty actionsDisabled = new SimpleBooleanProperty(true);
    private static Socket socket;
    private static ObjectOutputStream outStream;
    private static ObjectInputStream inStream;

    private static boolean isServerOn = true;

    public static ListProperty<Email> emailsInboxProperty() {
        return emailsInbox;
    }

    public static ObjectProperty<Color> serverStatusProperty() {
        return serverStatus;
    }

    static {
        emailsInbox.set(emailsInboxContent);
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

                    ClientGUIController.setSelectedEmail(null);
                } catch (Exception e) {
                    System.out.println("[startServiceThread] Server is still down: " + e.getMessage());
                }
            }
        }, 2, 1, TimeUnit.SECONDS);
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

    public static void startConnection() throws Exception {

        UserModel user = UserController.getUser();

        try {
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
            outStream = new ObjectOutputStream(socket.getOutputStream());

            socket.setSoTimeout(1000);
            inStream = new ObjectInputStream(socket.getInputStream());

            ConnectionRequest conn = new ConnectionRequest(user.getAddress(), user.getPassword(), ConnectionRequest.Status.CONNECT); // Creo l'oggetto da inviare per richiedere la connessione al server
            outStream.writeObject(conn); // Scrivo l'oggetto sullo stream di uscita

            Object obj = inStream.readObject();
            if (!(obj instanceof Response res)) throw new Exception("Invalid Response");

            if (!res.isSuccessful())
                throw new Exception(res.getMessage());

            emailsInboxContent.clear();
            emailsInboxContent.addAll(((ArrayList<Email>) res.getData()));
            emailsInboxContent.sort(Email::compareTo);
            setActionsDisabled(true);
        } catch (IOException e) {
            System.out.println("[startConnection] Connection Error: " + e.getMessage());
            throw new Exception("Connection Error");
        } finally {
            try {
                outStream.close();
                inStream.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("[startConnection] Error on closing: " + e.getMessage());
            }
        }
    }

    public static void endConnection() throws Exception {
        if (!isServerOn) throw new Exception("Server is down");

        try {
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
            outStream = new ObjectOutputStream(socket.getOutputStream());

            socket.setSoTimeout(1000);
            inStream = new ObjectInputStream(socket.getInputStream());

            ConnectionRequest conn = new ConnectionRequest(UserController.getUser().getAddress(), UserController.getUser().getPassword(), ConnectionRequest.Status.DISCONNECT); // Creo l'oggetto da inviare per richiedere la disconnessione al server
            outStream.writeObject(conn); // Scrivo l'oggetto sullo stream di uscita

            Object obj = inStream.readObject();
            if (!(obj instanceof Response res)) throw new Exception("Invalid Response");

            if (!res.isSuccessful())
                throw new Exception("Invalid Credentials");
        } catch (IOException e) {
            System.out.println("[endConnection] Connection Error: " + e.getMessage());
            throw new Exception("Connection Error");
        } finally {
            try {
                outStream.close();
                inStream.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("[endConnection] Error on closing: " + e.getMessage());
            }
        }
    }

    public static boolean fillInbox() throws Exception {
        if (!isServerOn) throw new Exception("Server is down");

        UserModel user = UserController.getUser();

        try {
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
            outStream = new ObjectOutputStream(socket.getOutputStream());

            socket.setSoTimeout(1000);
            inStream = new ObjectInputStream(socket.getInputStream());

            EmailRequest req = new EmailRequest(user.getAddress(), EmailRequest.RequestType.FILL_INBOX); // Creo l'oggetto da inviare per richiedere la connessione al server
            outStream.writeObject(req); // Scrivo l'oggetto sullo stream di uscita

            Object obj = inStream.readObject();
            if (!(obj instanceof Response res)) throw new Exception("Invalid Response");

            if (!res.isSuccessful()) throw new Exception(res.getMessage());

            ArrayList<Email> emails = (ArrayList<Email>) res.getData();
            if (emails.size() > 0) {
                emailsInboxContent.addAll(0, emails);
                emailsInboxContent.sort(Email::compareTo);
                return true;
            }

            return false;
        } catch (IOException e) {
            System.out.println("[fillInbox] Connection Error: " + e.getMessage());
            throw new Exception("Connection Error");
        } finally {
            try {
                outStream.close();
                inStream.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("[fillInbox] Error on closing: " + e.getMessage());
            }
        }
    }

    public static void deleteEmail(Email email) throws Exception {
        if (!isServerOn) throw new Exception("Server is down");

        UserModel user = UserController.getUser();
        try {
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
            outStream = new ObjectOutputStream(socket.getOutputStream());

            socket.setSoTimeout(1000);
            inStream = new ObjectInputStream(socket.getInputStream());

            EmailRequest conn = new EmailRequest(user.getAddress(), EmailRequest.RequestType.DELETE_FROM_INBOX, email); // Creo l'oggetto da inviare per richiedere la connessione al server
            outStream.writeObject(conn); // Scrivo l'oggetto sullo stream di uscita

            Object obj = inStream.readObject();
            if (!(obj instanceof Response res)) throw new Exception("Invalid Response");

            if (!res.isSuccessful())
                throw new Exception(res.getMessage());

            emailsInbox.remove(email);
        } catch (IOException e) {
            System.out.println("[deleteEmail] Connection Error: " + e.getMessage());
            throw new Exception("Connection Error");
        } finally {
            try {
                outStream.close();
                inStream.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("[deleteEmail] Error on closing: " + e.getMessage());
            }
        }
    }

    public static void sendEmail(Email email) throws Exception {
        if (!isServerOn) throw new Exception("Server is down");

        UserModel user = UserController.getUser();

        try {
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
            outStream = new ObjectOutputStream(socket.getOutputStream());

            socket.setSoTimeout(1000);
            inStream = new ObjectInputStream(socket.getInputStream());

            EmailRequest conn = new EmailRequest(user.getAddress(), EmailRequest.RequestType.SEND, email); // Creo l'oggetto da inviare per richiedere la connessione al server
            outStream.writeObject(conn); // Scrivo l'oggetto sullo stream di uscita

            Object obj = inStream.readObject();
            if (!(obj instanceof Response res)) throw new Exception("Invalid Response");

            if (!res.isSuccessful()) {
                throw new Exception("Wrong Recipients:\n" + res.getData().toString());
            }
        } catch (IOException e) {
            System.out.println("[sendEmail] Connection Error: " + e.getMessage());
            throw new Exception("Connection Error");
        } finally {
            try {
                outStream.close();
                inStream.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("[sendEmail] Error on closing: " + e.getMessage());
            }
        }
    }

    public static boolean isActionsDisabled() {
        return actionsDisabled.get();
    }

    public static BooleanProperty actionsDisabledProperty() {
        return actionsDisabled;
    }

    public static void setActionsDisabled(boolean actionsDisabled) {
        ConnectionController.actionsDisabled.set(actionsDisabled);
    }
}
