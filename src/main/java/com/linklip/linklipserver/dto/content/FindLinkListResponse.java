package com.linklip.linklipserver.dto.content;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class FindLinkListResponse {

    Page<ContentDto> pageDto;

    public FindLinkListResponse(Page<ContentDto> pageDto) {
        this.pageDto = pageDto;
    }
}
