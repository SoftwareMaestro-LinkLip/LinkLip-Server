package com.linklip.linklipserver.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SaveLinkRequest {

    @NotEmpty
    private String url;
}
