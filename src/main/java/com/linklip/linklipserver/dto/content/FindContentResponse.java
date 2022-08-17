package com.linklip.linklipserver.dto.content;

import com.linklip.linklipserver.domain.Content;
import lombok.Getter;

@Getter
public class FindContentResponse {

    private ContentDto content;

    public FindContentResponse(Content content) {
        this.content = new ContentDto(content);
    }

    public ContentDto getContent() {
        return new ContentDto(content);
    }
}
