package com.project.models;

import java.io.Serializable;

/**
 * This model is a standardized response from the Server to the Client
 */
public class Response implements Serializable {

    private final boolean success;
    private final String message;
    private final Object data;

    public Response(boolean success, String message, Object data) {
        if(message == null)
            throw new NullPointerException("Message cannot be null");
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Response(boolean success) {
        this(success, "", null);
    }

    public boolean isSuccessful() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
