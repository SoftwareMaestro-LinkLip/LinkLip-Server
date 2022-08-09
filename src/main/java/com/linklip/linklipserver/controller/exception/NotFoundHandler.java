package com.linklip.linklipserver.controller.exception;

import static com.linklip.linklipserver.constant.ErrorResponse.NOT_FOUND;

import com.linklip.linklipserver.dto.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class NotFoundHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ServerResponse handle404(Exception exception) {
        System.out.println("❗❗️ exception = " + exception);
        return new ServerResponse(
                NOT_FOUND.getStatus(), NOT_FOUND.getSuccess(), NOT_FOUND.getMessage());
    }
}
