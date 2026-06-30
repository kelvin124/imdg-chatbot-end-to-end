package com.example.demo.service.exception;

import java.util.Map;

public abstract class ApplicationException extends RuntimeException {

    protected Map<String, Object> properties;

    public ApplicationException(Throwable cause, Map<String, Object> properties) {
        super(cause);
        this.properties = properties;
    }

    public ApplicationException(String message, Map<String, Object> properties) {
        super(message);
        this.properties = properties;
    }

    public ApplicationException(String message, Throwable cause, Map<String, Object> properties) {
        super(message, cause);
        this.properties = properties;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
    
}
