package com.example.demo.service.exception;

import java.util.Map;

public class StowagePlanException extends ApplicationException {

    public StowagePlanException(Throwable cause, Map<String, Object> properties) {
        super(cause, properties);
    }

    public StowagePlanException(String message, Throwable cause, Map<String, Object> properties) {
        super(message, cause, properties);
    }

    public StowagePlanException(String message, Map<String, Object> properties) {
        super(message, properties);
    }

    public StowagePlanException(String message) {
        super(message, null);
    }

}
