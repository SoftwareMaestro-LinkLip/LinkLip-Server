package com.linklip.linklipserver.dto.content;

import lombok.Getter;

@Getter
public class FindContentResponse<T> {

    private final T content;

    public FindContentResponse(T content) {
        this.content = content;
    }
}
