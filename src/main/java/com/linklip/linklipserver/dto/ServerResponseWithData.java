package com.linklip.linklipserver.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@RequiredArgsConstructor
public class ServerApiResponse {

    private final int status;
    private final String message;
    private final Object data;

}
