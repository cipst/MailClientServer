package com.project.models;

public class EmailRequestModel implements java.io.Serializable {
    public enum RequestType {
        SEND,
        DELETE_FROM_INBOX,
        FILL_INBOX,
    }

    private final String requestingAddress;
    private final RequestType requestType;
    private final EmailSerializable email;

    public EmailRequestModel(String requestingAddress, RequestType requestType, EmailSerializable email) {
        this.requestingAddress = requestingAddress;
        this.requestType = requestType;
        this.email = email;
    }

    public EmailRequestModel(String requestingAddress, RequestType requestType) {
        this.requestingAddress = requestingAddress;
        this.requestType = requestType;
        this.email = null;
    }

    public String getRequestingAddress() {
        return requestingAddress;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public EmailSerializable getEmail() {
        return email;
    }

}
