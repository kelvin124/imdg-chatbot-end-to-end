package com.example.demo.service.exception;

import java.util.Map;

public class RecordNotFoundException extends ApplicationException {

    public RecordNotFoundException(Throwable cause, Map<String, Object> properties) {
        super(cause, properties);
    }

    public RecordNotFoundException(String message, Throwable cause, Map<String, Object> properties) {
        super(message, cause, properties);
    }

    public RecordNotFoundException(String message, Map<String, Object> properties) {
        super(message, properties);
    }

    public RecordNotFoundException(String message) {
        super(message, null);
    }

}
