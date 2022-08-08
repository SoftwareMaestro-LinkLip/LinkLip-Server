package com.linklip.linklipserver.dto.category;

import com.linklip.linklipserver.domain.Category;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCategoryRequest {

    @NotEmpty private String name;

    public Category toEntity() {
        return Category.builder().name(name).build();
    }
}
