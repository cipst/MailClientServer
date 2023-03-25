package com.project.server.model;

public class ConnectionRequestModel implements java.io.Serializable {

    private final String email;
    private final String password;
    private final int port;

    public ConnectionRequestModel(String email, String password, int port) {
        this.email = email;
        this.password = password;
        this.port = port;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

}
