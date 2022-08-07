package com.linklip.linklipserver.dto.category;

import java.util.List;
import lombok.Data;

@Data
public class FindCategoryResponse {
    List<CategoryDto> category;

    public FindCategoryResponse(List<CategoryDto> categoryList) {
        this.category = categoryList;
    }
}
