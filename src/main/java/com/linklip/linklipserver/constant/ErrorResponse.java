package com.linklip.linklipserver.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ErrorResponse {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "ERR01", "잘못된 요청입니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "ERR02", "요청 경로 오류"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ERR03", "서버 내부 오류"),
    INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "ERR04", "잘못된 Content Type입니다"),
    NOT_EXSIT_CONTENT_ID(HttpStatus.NOT_FOUND, "ERR05", "존재하지 않는 Content ID입니다"),
    NOT_EXSIT_CATEGORY_ID(HttpStatus.NOT_FOUND, "ERR06", "존재하지 않는 Category ID입니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "ERR07", "인증되지 않은 사용자입니다"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "ERR08", "만료된 Access 토큰입니다"),
    NOT_EXSIT_USER_ID(HttpStatus.NOT_FOUND, "ERR09", "존재하지 않는 User ID입니다"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "ERR10", "만료된 Refresh 토큰입니다"),
    ;

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
