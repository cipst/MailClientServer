package com.project.server.controller;

import com.project.models.*;
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
        if (!thread.isAlive())
            thread.start();
    }

    public void stopServer() {
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
                ResponseModel response = null;

                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                int clientPort = clientSocket.getPort();

                Object obj = in.readObject();
                if (obj instanceof ConnectionRequestModel request) {
                    response = handleConnectionRequest(request);
                } else if (obj instanceof EmailRequestModel email) {
                    response = handleEmailRequest(email);
                }
                out.writeObject(response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private ResponseModel handleConnectionRequest(ConnectionRequestModel request) {
            try {
                String email = request.getEmail();
                String password = request.getPassword();
                ConnectionRequestModel.Status status = request.getStatus();

                if (status == ConnectionRequestModel.Status.DISCONNECT) {
                    connectedClients.remove(email);
                    LogController.clientDisconnected(email);
                    return new ResponseModel(true, "Disconnection successful", null);
                }

                System.out.println("--- Handling connection request");
                LogController.loginRequest(email);

                if (!db.userExist(email)) {
                    LogController.loginDenied(email, "User not found");
                    return new ResponseModel(false, "User not found", null);
                }

                if (!db.checkCredentials(email, password)) {
                    LogController.loginDenied(email, "Wrong password");
                    return new ResponseModel(false, "Wrong password", null);
                }

                LogController.loginAccepted(email);
                connectedClients.add(email);
                return new ResponseModel(true, "Connection successful", null);

                //TODO: send emails outbox
                //TODO: send emails inbox
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        private ResponseModel handleEmailRequest(EmailRequestModel request) {
            switch (request.getRequestType()) {
                case SEND:
                    return sendEmail(request.getEmail());
                case DELETE_FROM_INBOX:
//                    return deleteFromInbox(request.getEmail());
                default:
                    return new ResponseModel(false, "Invalid request", null);
            }
        }

        private ResponseModel sendEmail(EmailSerializable email) {
            try {
                ArrayList<String> wrongRecipients = checkRecipients(email);
                if (wrongRecipients.size() > 0) {
                    LogController.emailRejected(email.getSender(), wrongRecipients);
                    return new ResponseModel(false, "Wrong recipients", wrongRecipients);
                }
                db.insertEmail(email);
                return new ResponseModel(true, "Email sent", null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private ArrayList<String> checkRecipients(EmailSerializable email) {
            boolean currentRecipientExists;
            ArrayList<String> wrongRecipients = new ArrayList<>();

            // check all recipients must be valid
            for (String recipient : email.getRecipients()) {
                currentRecipientExists = db.userExist(recipient);
                if (!currentRecipientExists) {
                    wrongRecipients.add(recipient);
                }
            }

            return wrongRecipients;
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
