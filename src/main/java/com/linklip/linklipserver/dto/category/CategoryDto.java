package com.linklip.linklipserver.dto.category;

import com.linklip.linklipserver.domain.Category;
import lombok.Builder;
import lombok.Data;

@Data
public class CategoryDto {

    private Long id;
    private String name;

    @Builder
    public CategoryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }

    public CategoryDto(CategoryDto category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
