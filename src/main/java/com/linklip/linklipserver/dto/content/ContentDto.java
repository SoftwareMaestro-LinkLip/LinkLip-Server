package com.linklip.linklipserver.dto.content;

import com.linklip.linklipserver.domain.Content;
import lombok.Data;

@Data
public class ContentDto {

    private Long id;
    private String url;
    private String linkImg;
    private String title;

    public ContentDto(Content content) {
        this.id = content.getId();
        this.url = content.getLinkUrl();
        this.linkImg = content.getLinkImg();
        this.title = content.getTitle();
    }
}
