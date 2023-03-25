package com.project.server.model;

import java.io.Serializable;

public class ResponseModel implements Serializable {

    private final boolean success;
    private final String message;
    private final Object data;

    public ResponseModel(boolean success, String message, Object data) {
        if(message == null)
            throw new NullPointerException("Message cannot be null");
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public ResponseModel(boolean success) {
        this(success, "Undefined", null);
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
