package com.linklip.linklipserver.dto.content;

import com.linklip.linklipserver.domain.content.Image;
import com.linklip.linklipserver.dto.category.CategoryDto;
import lombok.Getter;

@Getter
public class ImageDto extends ContentDto {

    private final String imageUrl;

    public ImageDto(Image content) {
        this.id = content.getId();
        this.type = content.getType();
        this.imageUrl = content.getImageUrl();
        if (content.getCategory() != null) {
            this.category = new CategoryDto(content.getCategory());
        }
    }
}
