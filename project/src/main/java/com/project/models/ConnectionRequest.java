package com.project.models;

/**
 * Client uses this model to communicate with the Server its data upon connection/disconnection
 */
public class ConnectionRequest implements java.io.Serializable {

    public enum Status {
        CONNECT, DISCONNECT
    }

    private final String email;
    private final String password;
    private final Status status;

    public ConnectionRequest(String email, String password, Status status) {
        this.status = status;
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Status getStatus() {
        return status;
    }
}
