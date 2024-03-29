package com.linklip.linklipserver.controller.handler;

import static com.linklip.linklipserver.constant.ErrorResponse.INTERNAL_ERROR;

import com.linklip.linklipserver.dto.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InternalErrorHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({RuntimeException.class})
    public ServerResponse handle500(Exception exception) {
        System.out.println("❗❗️ exception = " + exception);
        return new ServerResponse(
                INTERNAL_ERROR.getStatus(),
                INTERNAL_ERROR.getSuccess(),
                INTERNAL_ERROR.getMessage());
    }
}
