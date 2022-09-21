package com.linklip.linklipserver.config.oauth;

import com.linklip.linklipserver.config.auth.PrincipalDetails;
import com.linklip.linklipserver.service.TokenService;
import com.linklip.linklipserver.util.JwtTokenUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;

    @Value("${jwt.target-url}")
    private String targetUrl;

    @Value("${jwt.secret-key}")
    private String key;

    @Value("${jwt.access-token-expired-time-ms}")
    private Long accessTokenExpiredTime;

    @Value("${jwt.refresh-token-expired-time-ms}")
    private Long refreshTokenExpiredTime;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException {

        PrincipalDetails oAuth2User = (PrincipalDetails) authentication.getPrincipal();
        Long userId = oAuth2User.getUserId();

        String accessToken = JwtTokenUtils.generateToken(userId, key, accessTokenExpiredTime);
        String refreshToken = generateRefreshToken(userId);

        String url = makeRedirectUrl(targetUrl, accessToken, refreshToken);
        getRedirectStrategy().sendRedirect(request, response, url);
    }

    private String generateRefreshToken(Long userId) {
        String refreshToken = JwtTokenUtils.generateToken(userId, key, refreshTokenExpiredTime);
        tokenService.saveRefreshToken(userId, refreshToken);
        return refreshToken;
    }

    private String makeRedirectUrl(String targetUrl, String accessToken, String refreshToken) {
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build()
                .toUriString();
    }
}
