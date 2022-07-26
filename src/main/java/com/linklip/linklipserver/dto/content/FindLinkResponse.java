package com.linklip.linklipserver.dto.content;

import java.util.List;
import lombok.Data;

@Data
public class FindLinkResponse {

    List<ContentDto> contents;

    public FindLinkResponse(List<ContentDto> contents) {
        this.contents = contents;
    }
}
