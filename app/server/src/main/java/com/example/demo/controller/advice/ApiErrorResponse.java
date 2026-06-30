package com.example.demo.controller.advice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("status")
    private String status;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("properties")
    private Map<String, Object> properties;

    public ApiErrorResponse(String detail, String uri, Map<String, Object> properties, HttpStatus status, Instant timestamp) {
        this.detail = detail;
        this.uri = uri;
        this.properties = properties;
        this.status = status.getReasonPhrase();
        this.timestamp = timestamp;
    }

}
