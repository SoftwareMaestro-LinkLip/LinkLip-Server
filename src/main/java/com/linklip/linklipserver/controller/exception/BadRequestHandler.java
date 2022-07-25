package com.linklip.linklipserver.controller.exception;

import static com.linklip.linklipserver.constant.ErrorResponse.BAD_REQUEST;

import com.linklip.linklipserver.dto.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BadRequestHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentNotValidException.class
    })
    public ServerResponse handle400(Exception exception) {
        return new ServerResponse(
                BAD_REQUEST.getStatus(), BAD_REQUEST.getSuccess(), BAD_REQUEST.getMessage());
    }
}
