package com.project.server.model;

import com.project.models.Email;

public class EmailRequestModel implements java.io.Serializable {
    public enum RequestType {
        SEND,
        DELETE_FROM_INBOX,
        DELETE_FROM_OUTBOX
    };

    private final String requestingAddress;
    private final RequestType requestType;
    private final Email email;

    public EmailRequestModel(String requestingAddress, RequestType requestType, Email email) {
        this.requestingAddress = requestingAddress;
        this.requestType = requestType;
        this.email = email;
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
