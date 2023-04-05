package com.project.server.controller;

import com.project.models.ConnectionRequestModel;
import com.project.models.EmailRequestModel;
import com.project.models.EmailSerializable;
import com.project.models.ResponseModel;
import com.project.server.Database;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConnectionController {
    private static final int LISTENING_PORT = 1234;
    private final Database db;
    private HashMap<String, Integer> connectedClients;
    private static boolean isServerOn = false;
    private static ServerSocket serverSocket;
    private static ScheduledExecutorService executor;

    static {
        try {
            serverSocket = new ServerSocket(LISTENING_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ConnectionController(Database db) {
        connectedClients = new HashMap<>();
        this.db = db;
    }

    public void runServer() {
        System.out.println("--- Starting server");
        isServerOn = true;

        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            try {
                if (!isServerOn) return;
                Socket clientSocket = serverSocket.accept();
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.setDaemon(true);
                t.start();
            } catch (Exception e) {
                System.out.println("[runServer] Error: " + e.getMessage());
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public void stopServer() {
        isServerOn = false;
        connectedClients.clear();
        executor.shutdown();
        System.out.println("--- Stopping server");
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
            ObjectInputStream in = null;
            ObjectOutputStream out = null;
            try {
                clientSocket.setSoTimeout(2000);
                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                ResponseModel response = null;

                Object obj = in.readObject();
                if (obj instanceof ConnectionRequestModel request) {
                    response = handleConnectionRequest(request);
                } else if (obj instanceof EmailRequestModel email) {
                    response = handleEmailRequest(email);
                }
                out.writeObject(response);
            } catch (Exception e) {
                System.out.println("[ClientHandler] [run] Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    assert in != null;
                    assert out != null;
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private ResponseModel handleConnectionRequest(ConnectionRequestModel request) {
            try {
                if (!isServerOn) return new ResponseModel(false, "Server is off", null);

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
                connectedClients.put(email, -1);

                return fillInbox(email);
            } catch (Exception e) {
                return new ResponseModel(false, "Fail to handle connection request", null);
            }
        }

        private ResponseModel handleEmailRequest(EmailRequestModel request) {
            if (!isServerOn) return new ResponseModel(false, "Server is off", null);

            switch (request.getRequestType()) {
                case SEND:
                    System.out.println("--- Handling send request");
                    return sendEmail(request.getEmail());
                case FILL_INBOX:
                    System.out.println("--- Handling fill inbox request");
                    return fillInbox(request.getRequestingAddress());
                case DELETE_FROM_INBOX:
                    System.out.println("--- Handling delete request");
                    return deleteFromInbox(request.getEmail(), request.getRequestingAddress());
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
                if (db.insertEmail(email))
                    return new ResponseModel(true, "Email sent", null);
                else
                    return new ResponseModel(false, "Fail to send the email", null);
            } catch (Exception e) {
                return new ResponseModel(false, "Fail to send the email", null);
            }
        }

        private ResponseModel fillInbox(String account) {
            try {
                int lastWrittenId = db.readStats(account);
                int lastInboxId = connectedClients.get(account);
                ArrayList<EmailSerializable> inbox = new ArrayList<>();

                if (lastInboxId < lastWrittenId) {
                    ArrayList<EmailSerializable> emails = db.readAllEmails(account);

                    emails.removeIf(s -> s.getId() <= lastInboxId);

                    inbox = emails;
                    emails.sort(EmailSerializable::compareTo);
                    connectedClients.put(account, lastWrittenId);
                }

                return new ResponseModel(true, "Inbox filled", inbox);
            } catch (Exception e) {
                return new ResponseModel(false, "Fail to fill the inbox", null);
            }
        }

        private ResponseModel deleteFromInbox(EmailSerializable email, String account) {
            try {
                if (db.deleteEmail(email, account)) {
                    LogController.emailDeleted(account);
                    return new ResponseModel(true, "Email deleted", null);
                } else {
                    LogController.failEmailDeleted(account);
                    return new ResponseModel(false, "Fail to delete the email", null);
                }
            } catch (Exception e) {
                return new ResponseModel(false, "Fail to delete the email", null);
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
