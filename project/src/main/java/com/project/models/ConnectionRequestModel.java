package com.project.models;

public class ConnectionRequestModel implements java.io.Serializable {

    public enum Status {
        CONNECT, DISCONNECT
    }

    private final String email;
    private final String password;
    private final Status status;

    public ConnectionRequestModel(String email, String password, Status status) {
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
