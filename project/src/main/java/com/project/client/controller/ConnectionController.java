package com.project.client.controller;

import com.project.client.model.UserModel;
import com.project.models.ConnectionRequestModel;
import com.project.models.EmailRequestModel;
import com.project.models.EmailSerializable;
import com.project.models.ResponseModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;


public class ConnectionController {

    private static final int CONNECTION_PORT = 1234;
    private static ListProperty<EmailSerializable> emailsInbox = new SimpleListProperty<>();
    private static ObservableList<EmailSerializable> emailsInboxContent = FXCollections.observableArrayList();
    private static ObjectProperty<Color> serverStatus = new SimpleObjectProperty<>(Color.LAWNGREEN);
    private static Socket socket;
    private static ObjectOutputStream outStream;
    private static ObjectInputStream inStream;

    private static boolean isServerOn = true;
    private static Thread fillInboxOrReconnectionThread;

    static {
        emailsInbox.set(emailsInboxContent);
    }

    /**
     * It starts a new Thread that either fills the inbox or tries to reconnect to the server
     * depending on the server status
     */
    public static void startServiceThread() {
        fillInboxOrReconnectionThread = new Thread(() -> {
            try {
                while (true) {
                    if (isServerOn) {
                        System.out.println("Filling inbox");
                        if (!ConnectionController.fillInbox()) {
                            System.out.println("Server is down");
                            changeServerStatus(false);
                        } else {
                            System.out.println("Server is up thanks to fillInbox call");
                        }
                    } else {
                        System.out.println("Trying to reconnect");
                        try {
                            ConnectionController.startConnection();
                            System.out.println("Server is up thanks to startConnection call");
                            changeServerStatus(true);
                        } catch (Exception e) {
                            System.out.println("Server is still down");
                        }
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        fillInboxOrReconnectionThread.setDaemon(true);
        fillInboxOrReconnectionThread.start();
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

    public static ListProperty<EmailSerializable> emailsInboxProperty() {
        return emailsInbox;
    }

    public static Color getServerStatus() {
        return serverStatus.get();
    }

    public static ObjectProperty<Color> serverStatusProperty() {
        return serverStatus;
    }

    public static void startConnection() throws Exception {
        UserModel user = UserController.getUser();

        System.out.println("[" + Thread.currentThread().getName() + "] threadConnection avviato");

        try {
//            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
            socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT), 2000);
            outStream = new ObjectOutputStream(socket.getOutputStream());

            socket.setSoTimeout(2000);
            inStream = new ObjectInputStream(socket.getInputStream());

            ConnectionRequestModel conn = new ConnectionRequestModel(user.getAddress(), user.getPassword(), ConnectionRequestModel.Status.CONNECT); // Creo l'oggetto da inviare per richiedere la connessione al server
            outStream.writeObject(conn); // Scrivo l'oggetto sullo stream di uscita

            Object res = inStream.readObject();
            if (res instanceof ResponseModel && ((ResponseModel) res).isSuccessful()) {
                emailsInboxContent.clear();
                emailsInboxContent.addAll((ArrayList<EmailSerializable>) ((ResponseModel) res).getData());
            } else
                throw new Exception(((ResponseModel) res).getMessage());
        } catch (IOException e) {
            throw new Exception("Connection Error");
        } finally {
            System.out.println("[" + Thread.currentThread().getName() + "] threadConnection terminato");
            try {
                outStream.close();
                inStream.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void endConnection() throws Exception {
        if (!isServerOn) throw new Exception("Server is down");

        try {
//            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
            socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT), 2000);
            outStream = new ObjectOutputStream(socket.getOutputStream());

            socket.setSoTimeout(2000);
            inStream = new ObjectInputStream(socket.getInputStream());

            ConnectionRequestModel conn = new ConnectionRequestModel(UserController.getUser().getAddress(), UserController.getUser().getPassword(), ConnectionRequestModel.Status.DISCONNECT); // Creo l'oggetto da inviare per richiedere la disconnessione al server
            outStream.writeObject(conn); // Scrivo l'oggetto sullo stream di uscita

            Object res = inStream.readObject();

            if (res instanceof ResponseModel && !((ResponseModel) res).isSuccessful())
                throw new Exception("Invalid Credentials");
        } catch (IOException e) {
            throw new Exception("Connection Error");
        } finally {
            System.out.println("[" + Thread.currentThread().getName() + "] threadConnection terminato");
            try {
                outStream.close();
                inStream.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static boolean fillInbox() {
        if (!isServerOn) return false;

        UserModel user = UserController.getUser();

        System.out.println("[" + Thread.currentThread().getName() + "] threadConnection avviato");
        System.out.println("IS SERVER ON: " + isServerOn);

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT), 2000);
            System.out.println("DOPO LA CREAZIONE DEL SOCKET");
//            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
            outStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("DOPO OUTPUT STREAM");
            socket.setSoTimeout(1000);
            inStream = new ObjectInputStream(socket.getInputStream());

            System.out.println("DOPO GLI STREAM");

            EmailRequestModel req = new EmailRequestModel(user.getAddress(), EmailRequestModel.RequestType.FILL_INBOX); // Creo l'oggetto da inviare per richiedere la connessione al server
            outStream.writeObject(req); // Scrivo l'oggetto sullo stream di uscita

            System.out.println("DOPO LA SCRITTURA");

            Object res = inStream.readObject();

            System.out.println("DOPO LA LETTURA");

            if (res instanceof ResponseModel && ((ResponseModel) res).isSuccessful()) {
                emailsInboxContent.addAll((ArrayList<EmailSerializable>) ((ResponseModel) res).getData());
                return true;
            } else {
                System.out.println("FillInbox non riuscito");
//                new Alert(Alert.AlertType.ERROR, "?").showAndWait();
                return false;
            }
        } catch (IOException e) {
//            new Alert(Alert.AlertType.ERROR, "Connection Error").showAndWait();
            System.out.println(e.getMessage() + " [threadConnection]");
//            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
//            new Alert(Alert.AlertType.ERROR, "Something went wrong\nTry later.").showAndWait();
            return false;
        } finally {
            System.out.println("[" + Thread.currentThread().getName() + "] threadConnection terminato");
            try {
                outStream.close();
                inStream.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
//                e.printStackTrace();
            }
        }
    }

    public static boolean deleteEmail(EmailSerializable email) throws Exception {
        if (!isServerOn) throw new Exception("Server is down");

        UserModel user = UserController.getUser();
        try {
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
            outStream = new ObjectOutputStream(socket.getOutputStream());

            socket.setSoTimeout(2000);
            inStream = new ObjectInputStream(socket.getInputStream());

            EmailRequestModel conn = new EmailRequestModel(user.getAddress(), EmailRequestModel.RequestType.DELETE_FROM_INBOX, email); // Creo l'oggetto da inviare per richiedere la connessione al server
            outStream.writeObject(conn); // Scrivo l'oggetto sullo stream di uscita

            Object res = inStream.readObject();
            if (res instanceof ResponseModel && ((ResponseModel) res).isSuccessful()) {
//                System.out.println("Connessione al server stabilita");
                new Alert(Alert.AlertType.INFORMATION, ((ResponseModel) res).getMessage()).showAndWait();

                emailsInbox.remove(email);
                return true;
            } else {
//                System.out.println("Connessione al server non stabilita");
                new Alert(Alert.AlertType.ERROR, "Something went wrong").showAndWait();
                return false;
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Connection Error").showAndWait();
            System.out.println(e.getMessage() + " [threadConnection]");
//            e.printStackTrace();
            return false;
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong\nTry later.").showAndWait();
            e.printStackTrace();
            return false;
        } finally {
            System.out.println("[" + Thread.currentThread().getName() + "] threadConnection terminato");
            try {
                outStream.close();
                inStream.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
//                e.printStackTrace();
            }
        }
    }

    public static void sendEmail(EmailSerializable email) throws Exception {
        if (!isServerOn) throw new Exception("Server is down");

        UserModel user = UserController.getUser();

        try {
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
            outStream = new ObjectOutputStream(socket.getOutputStream());

            socket.setSoTimeout(2000);
            inStream = new ObjectInputStream(socket.getInputStream());

            EmailRequestModel conn = new EmailRequestModel(user.getAddress(), EmailRequestModel.RequestType.SEND, email); // Creo l'oggetto da inviare per richiedere la connessione al server
            outStream.writeObject(conn); // Scrivo l'oggetto sullo stream di uscita

            Object res = inStream.readObject();
            if (res instanceof ResponseModel && !((ResponseModel) res).isSuccessful()) {
                throw new Exception("Wrong Recipients:\n" + ((ResponseModel) res).getData().toString());
            }
        } catch (IOException e) {
            throw new Exception("Connection Error");
        } finally {
            System.out.println("[" + Thread.currentThread().getName() + "] threadConnection terminato");
            try {
                outStream.close();
                inStream.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
//                e.printStackTrace();
            }
        }
    }

}
