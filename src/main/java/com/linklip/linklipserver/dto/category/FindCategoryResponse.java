package com.linklip.linklipserver.dto.category;

import com.linklip.linklipserver.dto.content.ContentDto;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class FindCategoryResponse {
    List<CategoryDto> category;

    public FindCategoryResponse(List<CategoryDto> categoryList) {
        this.category = categoryList;
    }
}
