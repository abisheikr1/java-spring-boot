package com.billing_software.billing_software.utils.exception;

public class MongoClientCreationException extends RuntimeException {
    public MongoClientCreationException(String message) {
        super(message);
    }

    public MongoClientCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
