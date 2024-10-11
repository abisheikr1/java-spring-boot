package com.billing_software.billing_software.utils.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

public class CustomExceptionWithData extends RuntimeException {
    private HttpStatus status = HttpStatus.FORBIDDEN;
    public Map<String, Object> data;

    public CustomExceptionWithData(HttpStatus status, String message, Map<String, Object> data) {
        super(message);
        this.status = status;
        this.data = data;
        this.data.put("error", message);
    }

    public HttpStatus getStatus() {
        return status;
    }

}
