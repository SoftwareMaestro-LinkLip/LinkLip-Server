package com.linklip.linklipserver.dto.content;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class FindLinkResponse {

    Page<ContentDto> pageDto;

    public FindLinkResponse(Page<ContentDto> pageDto) {
        this.pageDto = pageDto;
    }
}
