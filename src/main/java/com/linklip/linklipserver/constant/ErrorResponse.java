package com.linklip.linklipserver.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ErrorResponse {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "ERR01", "잘못된 요청입니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "ERR02", "요청 경로 오류"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ERR03", "서버 내부 오류"),
    INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "ERR04", "잘못된 Content Type입니다");

    @Getter private int status;

    @Getter private final String code;

    @Getter private final Boolean success = false;

    @Getter private final String message;

    private ErrorResponse(HttpStatus status, String code, String message) {
        this.status = status.value();
        this.code = code;
        this.message = message;
    }
}
