package com.linklip.linklipserver.dto.auth;

import lombok.Data;

@Data
public class ReissueTokenRequest {

    private String accessToken;
    private String refreshToken;
}
