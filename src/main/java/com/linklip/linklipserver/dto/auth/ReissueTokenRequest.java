package com.linklip.linklipserver.dto.auth;

import lombok.Getter;

@Getter
public class ReissueTokenRequest {

    private String accessToken;
    private String refreshToken;
}
