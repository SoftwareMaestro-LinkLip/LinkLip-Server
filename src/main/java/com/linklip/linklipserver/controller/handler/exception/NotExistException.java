package com.linklip.linklipserver.controller.handler.exception;

import lombok.Getter;

@Getter
public class NotExistException extends RuntimeException {

    public NotExistException(String message) {
        super(message);
    }
}
