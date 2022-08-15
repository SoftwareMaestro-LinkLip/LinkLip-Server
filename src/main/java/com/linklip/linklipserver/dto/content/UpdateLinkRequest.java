package com.linklip.linklipserver.dto.content;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateLinkRequest {
    @ApiModelProperty(required = true)
    @NotEmpty
    private String title;

    private Long categoryId;
}
