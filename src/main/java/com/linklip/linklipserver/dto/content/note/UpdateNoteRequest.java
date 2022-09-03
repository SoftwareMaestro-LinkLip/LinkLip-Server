package com.linklip.linklipserver.dto.content.note;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateNoteRequest {
    @ApiModelProperty(required = true)
    @NotEmpty
    private String text;

    private Long categoryId;
}
