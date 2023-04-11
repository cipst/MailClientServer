package com.project.models;

/**
 * Client uses this model to tell the Server which operation to perform
 */
public class EmailRequest implements java.io.Serializable {
    public enum RequestType {
        SEND,
        DELETE_FROM_INBOX,
        FILL_INBOX,
    }

    private final String requestingAddress;
    private final RequestType requestType;
    private final Email email;

    public EmailRequest(String requestingAddress, RequestType requestType, Email email) {
        this.requestingAddress = requestingAddress;
        this.requestType = requestType;
        this.email = email;
    }

    public EmailRequest(String requestingAddress, RequestType requestType) {
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

    public Email getEmail() {
        return email;
    }

}
