package com.project.server.controller;

import com.project.models.ConnectionRequest;
import com.project.models.EmailRequest;
import com.project.models.Email;
import com.project.models.Response;
import com.project.server.Database;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionController {
    private static final int LISTENING_PORT = 1234;
    private final Database db;
    private HashMap<String, Integer> connectedClients;
    private static boolean isServerOn = false;
    private static ServerSocket serverSocket;
    private ExecutorService executor;
    private final Object lock = new Object();

    public ConnectionController(Database db) {
        connectedClients = new HashMap<>();
        this.db = db;
    }

    public void runServer() {
        System.out.println("--- Starting server");
        isServerOn = true;
        try {
            serverSocket = new ServerSocket(LISTENING_PORT);
        } catch (Exception e) {
            System.out.println("[runServer] [serverSocket] Error: " + e.getMessage());
        }

        Thread thread = new Thread(() -> {
            try {
                Socket clientSocket = null;

                executor = Executors.newFixedThreadPool(10);

                while (true) {
                    synchronized (lock) {
                        if (!isServerOn) {
                            lock.wait();
                        }
                    }

                    clientSocket = serverSocket.accept();

//                    Thread t = new Thread(new ClientHandler(clientSocket));
//                    t.setDaemon(true);
//                    t.start();

                    executor.execute(new ClientHandler(clientSocket));

                }
            } catch (Exception e) {
                System.out.println("[runServer] [thread] Error: " + e.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void stopServer() {
        isServerOn = false;
        connectedClients.clear();

        synchronized (lock) {
            lock.notifyAll();
        }

        executor.shutdown();

        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("[stopServer] [serverSocket] Error: " + e.getMessage());
        }
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
                clientSocket.setSoTimeout(1000);
                clientSocket.setKeepAlive(true);

                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                Response response = null;

                Object obj = in.readObject();
                if (obj instanceof ConnectionRequest request) {
                    response = handleConnectionRequest(request);
                } else if (obj instanceof EmailRequest email) {
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
                } finally {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private Response handleConnectionRequest(ConnectionRequest request) {
            try {
                if (!isServerOn) return new Response(false, "Server is off", null);

                String email = request.getEmail();
                String password = request.getPassword();
                ConnectionRequest.Status status = request.getStatus();

                if (status == ConnectionRequest.Status.DISCONNECT) {
                    connectedClients.remove(email);
                    LogController.clientDisconnected(email);
                    return new Response(true, "Disconnection successful", null);
                }

                System.out.println("--- Handling connection request");
                LogController.loginRequest(email);

                if (!db.userExist(email)) {
                    LogController.loginDenied(email, "User not found");
                    return new Response(false, "User not found", null);
                }

                if (!db.checkCredentials(email, password)) {
                    LogController.loginDenied(email, "Wrong password");
                    return new Response(false, "Wrong password", null);
                }

                LogController.loginAccepted(email);
                connectedClients.put(email, -1);

                return fillInbox(email);
            } catch (Exception e) {
                return new Response(false, "Fail to handle connection request", null);
            }
        }

        private Response handleEmailRequest(EmailRequest request) {
            if (!isServerOn) return new Response(false, "Server is off", null);

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
                    return new Response(false, "Invalid request", null);
            }
        }

        private Response sendEmail(Email email) {
            try {
                ArrayList<String> wrongRecipients = checkRecipients(email);
                if (wrongRecipients.size() > 0) {
                    LogController.emailRejected(email.getSender(), wrongRecipients);
                    return new Response(false, "Wrong recipients", wrongRecipients);
                }
                if (db.insertEmail(email))
                    return new Response(true, "Email sent", null);
                else
                    return new Response(false, "Fail to send the email", null);
            } catch (Exception e) {
                return new Response(false, "Fail to send the email", null);
            }
        }

        private Response fillInbox(String account) {
            try {
                int lastWrittenId = db.readStats(account);
                int lastInboxId = connectedClients.get(account);
                ArrayList<Email> inbox = new ArrayList<>();

                if (lastInboxId < lastWrittenId) {
                    ArrayList<Email> emails = db.readAllEmails(account);

                    emails.removeIf(s -> s.getId() <= lastInboxId);

                    inbox = emails;

                    connectedClients.put(account, lastWrittenId);
                }

                return new Response(true, "Inbox filled", inbox);
            } catch (Exception e) {
                return new Response(false, "Fail to fill the inbox", null);
            }
        }

        private Response deleteFromInbox(Email email, String account) {
            try {
                if (db.deleteEmail(email, account)) {
                    LogController.emailDeleted(account);
                    return new Response(true, "Email deleted", null);
                } else {
                    LogController.failEmailDeleted(account);
                    return new Response(false, "Fail to delete the email", null);
                }
            } catch (Exception e) {
                return new Response(false, "Fail to delete the email", null);
            }
        }

        private ArrayList<String> checkRecipients(Email email) {
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
