package com.linklip.linklipserver.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SaveLinkRequest {
    @NotEmpty private String url;
    private String linkImg;
}
