package com.linklip.linklipserver.exception;

public class NotValidTokenException extends RuntimeException {

    public NotValidTokenException(String message) {
        super(message);
    }
}
