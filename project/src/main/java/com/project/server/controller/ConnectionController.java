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
    private HashMap<String, Integer> lastClientsIdInbox;
    private static boolean isServerOn = false;
    private static ServerSocket serverSocket;
    private ExecutorService executor;
    private final Object lock = new Object();

    public ConnectionController() {
        lastClientsIdInbox = new HashMap<>();
        this.db = new Database();
    }

    public static boolean isServerOn() {
        return isServerOn;
    }

    /**
     * Creates a new ServerSocket and stays listening for new connection.
     * If a connection is accepted, a new Thread in the ExecutorThreadPool
     * is executed to handle the given task
     * If the server is turned off, the synchronized part of the method stop the execution thanks to lock.wait()
     */
    public void runServer() {
        isServerOn = true;
        try {
            serverSocket = new ServerSocket(LISTENING_PORT);
        } catch (Exception e) {
            System.out.println("[runServer] [serverSocket] Error: " + e.getMessage());
        }

        //Server listening thread
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
        lastClientsIdInbox.clear();

        synchronized (lock) {
            lock.notifyAll();
        }

        executor.shutdown();

        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("[stopServer] [serverSocket] Error: " + e.getMessage());
        }
    }

    /**
     * This class handles the client Requests and returns Responses
     *
     * @see com.project.models.Response
     * @see com.project.models.ConnectionRequest
     * @see com.project.models.EmailRequest
     */
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
                } else {
                    response = new Response(false, "Invalid request");
                }
                out.writeObject(response);
            } catch (Exception e) {
                System.out.println("[ClientHandler] [run] Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if (in != null)
                        in.close();
                    if (out != null)
                        out.close();
                } catch (IOException e) {
                    System.out.println("[ClientHandler] [run] Error closing stream: " + e.getMessage());
                } finally {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        System.out.println("[ClientHandler] [run] Error closing client socket: " + e.getMessage());
                    }
                }
            }
        }

        /**
         * Handles the connection request from the client
         * If the request is a disconnection, the client is removed from the connectedClients
         * If the request is a connection, the client is added to the connectedClients and the inbox is filled
         *
         * @param request the request from the client
         * @return a Response object containing:
         * - the successfulness of the request, a message and the inbox the connection was successful;
         * - only the successfulness and the message otherwise.
         */
        private Response handleConnectionRequest(ConnectionRequest request) {
            try {
                if (!isServerOn) return new Response(false, "Server is off");

                String email = request.getEmail();
                String password = request.getPassword();
                ConnectionRequest.Status status = request.getStatus();

                if (status == ConnectionRequest.Status.DISCONNECT) {
                    lastClientsIdInbox.remove(email);
                    LogController.clientDisconnected(email);
                    return new Response(true, "Disconnection successful");
                }

                LogController.loginRequest(email);

                if (!db.userExist(email)) {
                    LogController.loginDenied(email, "User not found");
                    return new Response(false, "User not found");
                }

                if (!db.checkCredentials(email, password)) {
                    LogController.loginDenied(email, "Wrong password");
                    return new Response(false, "Wrong password");
                }

                LogController.loginAccepted(email);
                lastClientsIdInbox.put(email, -1);

                return fillInbox(email);
            } catch (Exception e) {
                return new Response(false, "Fail to handle connection request");
            }
        }

        /**
         * Handles the email request from the client
         * Based on the request type, this method will call the right method to handle the specific request
         *
         * @param request the request from the client
         * @return a Response object passed by the called method
         */
        private Response handleEmailRequest(EmailRequest request) {
            if (!isServerOn) return new Response(false, "Server is off");

            switch (request.getRequestType()) {
                case SEND:
                    return sendEmail(request.getEmail());
                case FILL_INBOX:
                    return fillInbox(request.getRequestingAddress());
                case DELETE_FROM_INBOX:
                    return deleteFromInbox(request.getEmail(), request.getRequestingAddress());
                default:
                    return new Response(false, "Invalid request");
            }
        }

        /**
         * Sends an email to the recipients
         *
         * @param email the email to send
         * @return a Response object containing:
         * - the successfulness of the request, a message and the list of wrong addresses if any;
         * - only the successfulness and the message otherwise.
         */
        private Response sendEmail(Email email) {
            try {
                ArrayList<String> wrongRecipients = checkRecipients(email);
                if (wrongRecipients.size() > 0) {
                    LogController.emailRejected(email.getSender(), wrongRecipients);
                    return new Response(false, "Wrong recipients", wrongRecipients);
                }
                if (db.insertEmail(email))
                    return new Response(true, "Email sent");
                else
                    return new Response(false, "Fail to send the email");
            } catch (Exception e) {
                return new Response(false, "Fail to send the email");
            }
        }

        /**
         * Retrieve the emails from the database and fill the client inbox
         * The inbox is filled only if there are new emails
         * The last id of the inbox is stored in the lastClientsIdInbox map
         * The last id received from the database is stored in the account stats file
         * If the last id of the inbox is less than the last id received from the database, the inbox is filled
         *
         * @param account the account of the client
         * @return a Response object containing:
         * - the successfulness of the request, a message and the inbox if any;
         * - only the successfulness and the message otherwise.
         */
        private Response fillInbox(String account) {
            try {
                int lastWrittenId = db.readStats(account);
                int lastInboxId = lastClientsIdInbox.get(account);
                ArrayList<Email> inbox = new ArrayList<>();

                if (lastInboxId < lastWrittenId) {
                    ArrayList<Email> emails = db.readAllEmails(account);

                    emails.removeIf(s -> s.getId() <= lastInboxId);

                    inbox = emails;

                    lastClientsIdInbox.put(account, lastWrittenId);
                }

                return new Response(true, "Inbox filled", inbox);
            } catch (Exception e) {
                return new Response(false, "Fail to fill the inbox");
            }
        }

        private Response deleteFromInbox(Email email, String account) {
            try {
                if (db.deleteEmail(email, account)) {
                    LogController.emailDeleted(account);
                    return new Response(true, "Email deleted");
                } else {
                    LogController.failEmailDeleted(account);
                    return new Response(false, "Fail to delete the email");
                }
            } catch (Exception e) {
                return new Response(false, "Fail to delete the email");
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
