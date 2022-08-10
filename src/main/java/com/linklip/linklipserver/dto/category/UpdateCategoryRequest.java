package com.linklip.linklipserver.dto.category;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateCategoryRequest {

    @ApiModelProperty(required = true)
    @NotEmpty
    private String name;
}
