package com.linklip.linklipserver.config.oauth;

import com.linklip.linklipserver.config.util.JwtTokenUtils;
import com.linklip.linklipserver.repository.UserRepository;
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

    @Value("${jwt.target-url}")
    private String targetUrl;

    @Value("${jwt.secret-key}")
    private String key;

    @Value("${jwt.token-expired-time-ms}")
    private Long expiredTime;

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException {

        String token = JwtTokenUtils.generateToken(authentication, key, expiredTime);

        String url = makeRedirectUrl(targetUrl, token);

        getRedirectStrategy().sendRedirect(request, response, url);
    }

    private String makeRedirectUrl(String targetUrl, String token) {
        return UriComponentsBuilder.fromUriString(targetUrl + token).build().toUriString();
    }
}
