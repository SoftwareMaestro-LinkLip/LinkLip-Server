package com.linklip.linklipserver.dto.content;

import com.linklip.linklipserver.dto.category.CategoryDto;
import lombok.Getter;

@Getter
public class ContentDto {

    public String type;
    public Long id;
    public CategoryDto category;
}
