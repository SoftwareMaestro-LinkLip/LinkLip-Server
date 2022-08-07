package com.linklip.linklipserver.dto.category;

import java.util.ArrayList;
import java.util.List;

public class FindCategoryResponse {
    private List<CategoryDto> category;

    public FindCategoryResponse(List<CategoryDto> categoryList) {
        this.category = new ArrayList<>(categoryList);
    }

    public List<CategoryDto> getCategory() {
        return new ArrayList<>(category);
    }
}
