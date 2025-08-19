package com.test.transaction.exception;

public abstract class CustomException extends RuntimeException {


    public CustomException(String message) {
        super(message);
    }
}
