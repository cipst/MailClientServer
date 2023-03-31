package com.project.models;

public class ConnectionRequestModel implements java.io.Serializable {

    private final String email;
    private final String password;

    public ConnectionRequestModel(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
