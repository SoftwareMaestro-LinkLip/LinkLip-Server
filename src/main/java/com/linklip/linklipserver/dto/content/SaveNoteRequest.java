package com.linklip.linklipserver.dto.content;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SaveNoteRequest {
    @ApiModelProperty(required = true)
    @NotEmpty
    private String text;

    private Long categoryId;
}
