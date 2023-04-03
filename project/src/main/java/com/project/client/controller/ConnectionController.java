package com.project.client.controller;

import com.project.client.model.UserModel;
import com.project.models.ConnectionRequestModel;
import com.project.models.Email;
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


public class ConnectionController {

    private static final int CONNECTION_PORT = 1234;
    private static ListProperty<Email> emailsInbox = new SimpleListProperty<>();
    private static ObservableList<Email> emailsInboxContent = FXCollections.observableArrayList();
    private static Socket socket = null;

    static {
        emailsInbox.set(emailsInboxContent);
//        initConnection();
//        fillInbox();
    }

    public static ListProperty<Email> emailsInboxProperty() {
        return emailsInbox;
    }

    public static boolean startConnection() {
        UserModel user = UserController.getUser();

        System.out.println("[" + Thread.currentThread().getName() + "] threadConnection avviato");
        ObjectOutputStream outStream = null;
        ObjectInputStream inStream = null;
        try {
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), CONNECTION_PORT); // Creo il socket per inviare i dati al server
            outStream = new ObjectOutputStream(socket.getOutputStream()); // Recupero lo stream di uscita verso il socket

            //TODO USER MODEL
            ConnectionRequestModel conn = new ConnectionRequestModel(user.getAddress(), user.getPassword()); // Creo l'oggetto da inviare per richiedere la connessione al server
            outStream.writeObject(conn); // Scrivo l'oggetto sullo stream di uscita

            socket.setSoTimeout(2000);
            inStream = new ObjectInputStream(socket.getInputStream());
            Object res = inStream.readObject();
            if (res instanceof ResponseModel && ((ResponseModel) res).isSuccessful()) {
                System.out.println("Connessione al server stabilita");
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
            new Alert(Alert.AlertType.ERROR, "Something went wrong\nTry later.").showAndWait();
            return false;
        } finally {
            System.out.println("[" + Thread.currentThread().getName() + "] threadConnection terminato");
        }
//                finally {
//                    if (s != null) {                                                                                // Chiudo il socket se l'ho aperto correttamente
//                        try {
//                            s.close();
//                            System.out.println("Connessione al socket chiusa [threadConnection]");
//                        } catch (IOException ex) {
//                            System.out.println(ex.getMessage());
//                        }
//                    }
//                }
    }

    private static void fillInbox() {
        try {
            //TODO: get emails from server

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void deleteEmail(Email email) {

        emailsInbox.remove(email);
        //TODO: delete email from server
        new Alert(Alert.AlertType.INFORMATION, "Email eliminated").showAndWait();
    }

    public static void sendEmail(Email email) {

        //TODO: send email to server
        new Alert(Alert.AlertType.INFORMATION, String.format("Email sended\nFrom:%s\nTo:%s\nSubject:%s\nMessage:%s\n", email.getSender(), email.getRecipients().get(0), email.getSubject(), email.getMessage())).showAndWait();
        emailsInboxContent.add(email);
    }
}
