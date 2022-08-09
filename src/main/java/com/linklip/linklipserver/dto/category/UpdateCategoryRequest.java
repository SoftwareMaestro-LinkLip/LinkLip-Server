package com.linklip.linklipserver.dto.category;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateCategoryRequest {

    @NotEmpty private String name;
}
