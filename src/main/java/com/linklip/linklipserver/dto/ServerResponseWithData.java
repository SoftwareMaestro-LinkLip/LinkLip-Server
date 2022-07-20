package com.linklip.linklipserver.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ServerResponseWithData {

    private final int status;
    private final Boolean success;
    private final String message;
    private final Object data;

}
