package com.project.server.controller;

import com.project.models.Email;
import com.project.server.Database;
import com.project.server.model.ClientModel;
import com.project.server.model.ConnectionRequestModel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;

public class ConnectionController {
    private final int LISTENING_PORT = 1234;
    private final Database db;
    private HashMap<String, ClientModel> connectedClients;
    private static boolean isServerOn = false;

    public ConnectionController(Database db) {
        connectedClients = new HashMap<>();
        this.db = db;
    }

    private void runServer(){
        try{
            ServerSocket serverSocket = new ServerSocket(LISTENING_PORT);
            Socket clientSocket = null;

            while (isServerOn){
                clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static boolean isIsServerOn() {
        return isServerOn;
    }

    public static void setIsServerOn(boolean isServerOn) {
        ConnectionController.isServerOn = isServerOn;
    }

    private final class ClientHandler implements Runnable {
        private final Socket clientSocket;

        private ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                int clientPort = clientSocket.getPort();

                Object obj = in.readObject();
                if (obj instanceof ConnectionRequestModel request) {
                    handleNewConnection(request);
                } else if (obj instanceof Email email) {
                    //TODO: handle email HERE
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void handleNewConnection(ConnectionRequestModel request) {
            try {
                String email = request.getEmail();
                String password = request.getPassword();
                int port = request.getPort();

                if(!db.userExist(email)){
                    throw new Exception("User not found");
                }

                //TODO: check password THEN add client

                if(!db.checkCredentials(email, password)){
                    throw new Exception("Wrong password");
                }
                connectedClients.put(email,new ClientModel(clientSocket.getInetAddress().getHostAddress(),port));
//                        db.addClient(email, clientSocket);

                //TODO: send emails outbox
                //TODO: send emails inbox
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Socket clientSocket() {
            return clientSocket;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            ClientHandler that = (ClientHandler) obj;
            return Objects.equals(this.clientSocket, that.clientSocket);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clientSocket);
        }

        @Override
        public String toString() {
            return "ClientHandler[" +
                    "clientSocket=" + clientSocket + ']';
        }

    }
}
