package com.linklip.linklipserver.dto.content;

import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.dto.category.CategoryDto;
import lombok.Getter;

@Getter
public class ContentDto {

    private final Long id;
    private final String url;
    private final String linkImg;
    private final String title;
    private CategoryDto category = null;

    public ContentDto(Content content) {
        this.id = content.getId();
        this.url = content.getLinkUrl();
        this.linkImg = content.getLinkImg();
        this.title = content.getTitle();
        if (content.getCategory() != null) {
            this.category = new CategoryDto(content.getCategory());
        }
    }

    public ContentDto(ContentDto content) {
        this.id = content.getId();
        this.url = content.getUrl();
        this.linkImg = content.getLinkImg();
        this.title = content.getTitle();
        if (content.getCategory() != null) {
            this.category = new CategoryDto(content.getCategory());
        }
    }

    public CategoryDto getCategory() {
        if (category != null) {
            return new CategoryDto(category);
        }
        return null;
    }
}
