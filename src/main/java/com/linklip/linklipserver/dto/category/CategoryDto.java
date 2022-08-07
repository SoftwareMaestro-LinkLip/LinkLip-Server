package com.linklip.linklipserver.dto.category;

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
}
