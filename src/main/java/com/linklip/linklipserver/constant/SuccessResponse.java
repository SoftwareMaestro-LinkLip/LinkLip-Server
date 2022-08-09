package com.linklip.linklipserver.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum SuccessResponse {
    SAVE_LINK_SUCCESS(HttpStatus.CREATED, "OK1", "링크 저장 완료"),
    FIND_LINK_SUCCESS(HttpStatus.OK, "OK2", "검색결과 응답 완료"),
    CREATE_CATEGORY_SUCCESS(HttpStatus.CREATED, "OK3", "카테고리 생성 완료"),
    GET_CATEGORY_SUCCESS(HttpStatus.OK, "OK4", "카테고리 조회 완료"),
    UPDATE_CATEGORY_SUCCESS(HttpStatus.OK, "OK5", "카테고리 수정 완료");

    @Getter private int status;

    @Getter private final String code;

    @Getter private final Boolean success = true;

    @Getter private final String message;

    private SuccessResponse(HttpStatus status, String code, String message) {
        this.status = status.value();
        this.code = code;
        this.message = message;
    }
}
