package com.project.server.model;

public class ClientModel {
    private final String address;
    private final int port;

    public ClientModel(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
