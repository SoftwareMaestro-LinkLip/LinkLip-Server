package com.linklip.linklipserver.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum SuccessResponse {
    SAVE_LINK_SUCCESS(HttpStatus.CREATED, "OK1", "링크 저장 완료");

    @Getter
    private int status;

    @Getter
    private final String code;

    @Getter
    private final String message;

    private SuccessResponse(HttpStatus status, String code, String message) {
        this.status = status.value();
        this.code = code;
        this.message = message;
    }
}
