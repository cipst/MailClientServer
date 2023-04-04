package com.project.client.controller;

import com.project.client.model.UserModel;
import com.project.models.ConnectionRequestModel;
import com.project.models.EmailRequestModel;
import com.project.models.EmailSerializable;
import com.project.models.ResponseModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;


public class ConnectionController {

    private static final int CONNECTION_PORT = 1234;
    private static ListProperty<EmailSerializable> emailsInbox = new SimpleListProperty<>();
    private static ObservableList<EmailSerializable> emailsInboxContent = FXCollections.observableArrayList();
    private static Socket socket;
    private static ObjectOutputStream outStream;
    private static ObjectInputStream inStream;

    static {
        emailsInbox.set(emailsInboxContent);
//        initConnection();
//        fillInbox();
    }

    public static ListProperty<EmailSerializable> emailsInboxProperty() {
        return emailsInbox;
    }

    public static boolean startConnection() {
        UserModel user = UserController.getUser();

        System.out.println("[" + Thread.currentThread().getName() + "] threadConnection avviato");

        try {
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());

            ConnectionRequestModel conn = new ConnectionRequestModel(user.getAddress(), user.getPassword(), ConnectionRequestModel.Status.CONNECT); // Creo l'oggetto da inviare per richiedere la connessione al server
            outStream.writeObject(conn); // Scrivo l'oggetto sullo stream di uscita

            socket.setSoTimeout(2000);
            Object res = inStream.readObject();
            if (res instanceof ResponseModel && ((ResponseModel) res).isSuccessful()) {
                System.out.println("Connessione al server stabilita");

                emailsInboxContent.addAll((ArrayList<EmailSerializable>) ((ResponseModel) res).getData());
                return true;
            } else {
                System.out.println("Connessione al server non stabilita");
                new Alert(Alert.AlertType.ERROR, "Invalid Credentials").showAndWait();
                return false;
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Connection Error").showAndWait();
            System.out.println(e.getMessage() + " [threadConnection]");
//            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong\nTry later.").showAndWait();
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

    public static boolean endConnection() {
        try {
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());

            ConnectionRequestModel conn = new ConnectionRequestModel(UserController.getUser().getAddress(), UserController.getUser().getPassword(), ConnectionRequestModel.Status.DISCONNECT); // Creo l'oggetto da inviare per richiedere la disconnessione al server
            outStream.writeObject(conn); // Scrivo l'oggetto sullo stream di uscita

            socket.setSoTimeout(50000);
            Object res = inStream.readObject();

            if (res instanceof ResponseModel && ((ResponseModel) res).isSuccessful()) {
                System.out.println("Disconnessione dal server stabilita");
                return true;
            } else {
                System.out.println("Disconnessione dal server non stabilita");
                new Alert(Alert.AlertType.ERROR, "Invalid Credentials").showAndWait();
                return false;
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Connection Error").showAndWait();
            System.out.println(e.getMessage() + " [threadConnection]");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong\nTry later.").showAndWait();
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

    private static void fillInbox() {
        try {
            //TODO: get emails from server

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static boolean deleteEmail(EmailSerializable email) {
        //TODO: TEST ME PLS
//        new Alert(Alert.AlertType.INFORMATION, "Email eliminated").showAndWait();
        UserModel user = UserController.getUser();
        try {
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());

            EmailRequestModel conn = new EmailRequestModel(user.getAddress(), EmailRequestModel.RequestType.DELETE_FROM_INBOX, email); // Creo l'oggetto da inviare per richiedere la connessione al server
            outStream.writeObject(conn); // Scrivo l'oggetto sullo stream di uscita

            socket.setSoTimeout(2000);
            Object res = inStream.readObject();
            if (res instanceof ResponseModel && ((ResponseModel) res).isSuccessful()) {
//                System.out.println("Connessione al server stabilita");
                new Alert(Alert.AlertType.INFORMATION, ((ResponseModel) res).getMessage()).showAndWait();

//                emailsInbox.remove(email);
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

    public static boolean sendEmail(EmailSerializable email) {
        UserModel user = UserController.getUser();

        try {
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT);
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());

            EmailRequestModel conn = new EmailRequestModel(user.getAddress(), EmailRequestModel.RequestType.SEND, email); // Creo l'oggetto da inviare per richiedere la connessione al server
            outStream.writeObject(conn); // Scrivo l'oggetto sullo stream di uscita

            socket.setSoTimeout(2000);
            Object res = inStream.readObject();
            if (res instanceof ResponseModel && ((ResponseModel) res).isSuccessful()) {
//                System.out.println("Connessione al server stabilita");
                new Alert(Alert.AlertType.INFORMATION, String.format("Email sended\nFrom:%s\nTo:%s\nSubject:%s\nMessage:%s\n", email.getSender(), email.getRecipients().get(0), email.getSubject(), email.getMessage())).showAndWait();
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
//
//        emailsInboxContent.add(email);
    }
}
