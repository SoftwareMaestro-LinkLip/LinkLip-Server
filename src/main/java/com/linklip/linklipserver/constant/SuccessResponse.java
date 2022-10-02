package com.linklip.linklipserver.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum SuccessResponse {
    SAVE_LINK_SUCCESS(HttpStatus.CREATED, "OK1", "링크 저장 완료"),
    FIND_CONTENT_LIST_SUCCESS(HttpStatus.OK, "OK2", "검색결과 응답 완료"),
    CREATE_CATEGORY_SUCCESS(HttpStatus.CREATED, "OK3", "카테고리 생성 완료"),
    GET_CATEGORY_SUCCESS(HttpStatus.OK, "OK4", "카테고리 조회 완료"),
    UPDATE_CATEGORY_SUCCESS(HttpStatus.OK, "OK5", "카테고리 수정 완료"),
    UPDATE_LINK_SUCCESS(HttpStatus.OK, "OK6", "링크 수정 완료"),
    FIND_CONTENT_SUCCESS(HttpStatus.OK, "OK7", "컨텐츠 상세보기 응답 완료"),
    DELETE_CATEGORY_SUCCESS(HttpStatus.OK, "OK8", "카테고리 삭제 완료"),
    DELETE_CONTENT_SUCCESS(HttpStatus.OK, "OK9", "컨텐츠 삭제 완료"),
    SAVE_NOTE_SUCCESS(HttpStatus.CREATED, "OK10", "메모 저장 완료"),
    UPDATE_NOTE_SUCCESS(HttpStatus.OK, "OK11", "메모 수정 완료"),
    REISSUE_TOKEN_SUCCESS(HttpStatus.OK, "OK12", "토큰 재발급 완료"),
    SAVE_IMAGE_SUCCESS(HttpStatus.CREATED, "OK12", "사진 저장 완료"),
    ;

    @Getter private final int status;

    @Getter private final String code;

    @Getter private final Boolean success = true;

    @Getter private final String message;

    SuccessResponse(HttpStatus status, String code, String message) {
        this.status = status.value();
        this.code = code;
        this.message = message;
    }
}
