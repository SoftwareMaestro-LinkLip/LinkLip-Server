package com.linklip.linklipserver.controller.exception;

import com.linklip.linklipserver.dto.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.linklip.linklipserver.constant.ErrorResponse.BAD_REQUEST;


@RestControllerAdvice
public class BadRequestHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ServerResponse handle400(MethodArgumentNotValidException exception) {
        return new ServerResponse(BAD_REQUEST.getStatus(), BAD_REQUEST.getMessage());
    }
}
