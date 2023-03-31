package com.project.server.controller;

import com.project.models.ConnectionRequestModel;
import com.project.models.Email;
import com.project.models.ResponseModel;
import com.project.server.Database;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class ConnectionController {
    private final int LISTENING_PORT = 1234;
    private final Database db;
    private ArrayList<String> connectedClients;
    private static boolean isServerOn = false;

    private Thread thread;

    public ConnectionController(Database db) {
        connectedClients = new ArrayList<>();
        this.db = db;
        thread = new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(LISTENING_PORT);
                Socket clientSocket = null;

                while (isServerOn) {
                    clientSocket = serverSocket.accept();
                    new Thread(new ClientHandler(clientSocket)).start();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void runServer() {
        isServerOn = true;
        thread.start();
    }

    public void stopServer() {
        thread.interrupt();
        isServerOn = false;
    }

    public static boolean isServerOn() {
        return isServerOn;
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
                    ResponseModel response = handleConnectionRequest(request);
                    out.writeObject(response);
                } else if (obj instanceof Email email) {
                    //TODO: handle email HERE
                    //handleMailRequest(...);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private ResponseModel handleConnectionRequest(ConnectionRequestModel request) {
            try {
                System.out.println("--- Handling connection request");
                String email = request.getEmail();
                String password = request.getPassword();

                if (!db.userExist(email)) {
//                    throw new Exception("User not found");
                    return new ResponseModel(false, "User not found", null);
                }

                if (!db.checkCredentials(email, password)) {
//                    throw new Exception("Wrong password");
                    return new ResponseModel(false, "Wrong password", null);
                }
                connectedClients.add(email);

                return new ResponseModel(true, "Connection successful", null);

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
