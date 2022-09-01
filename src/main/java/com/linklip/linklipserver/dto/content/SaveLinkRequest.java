package com.linklip.linklipserver.dto.content;

import com.linklip.linklipserver.service.CategoryService;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SaveLinkRequest {

    private CategoryService categoryService;

    @ApiModelProperty(required = true)
    @NotEmpty
    private String url;

    private String linkImg;
    private String title;
    private String text;

    private Long categoryId;
}
