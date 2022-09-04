package com.linklip.linklipserver.dto.content;

import com.linklip.linklipserver.domain.Link;
import com.linklip.linklipserver.dto.category.CategoryDto;
import lombok.Getter;

@Getter
public class LinkDto extends ContentDto {

    private final String url;
    private final String linkImg;
    private final String title;

    public LinkDto(Link content) {
        this.id = content.getId();
        this.type = content.getType();
        this.url = content.getLinkUrl();
        this.linkImg = content.getLinkImg();
        this.title = content.getTitle();
        if (content.getCategory() != null) {
            this.category = new CategoryDto(content.getCategory());
        }
    }
}
