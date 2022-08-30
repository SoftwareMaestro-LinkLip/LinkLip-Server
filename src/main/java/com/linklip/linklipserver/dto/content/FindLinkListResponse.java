package com.linklip.linklipserver.dto.content;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class FindLinkListResponse {

    Page<?> pageDto;

    public FindLinkListResponse(Page<?> pageDto) {
        this.pageDto = pageDto;
    }
}
