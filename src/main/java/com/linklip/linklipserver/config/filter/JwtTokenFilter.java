package com.linklip.linklipserver.config.filter;

import static com.linklip.linklipserver.constant.ErrorResponse.EXPIRED_ACCESS_TOKEN;

import com.google.common.net.HttpHeaders;
import com.linklip.linklipserver.domain.User;
import com.linklip.linklipserver.service.UserService;
import com.linklip.linklipserver.util.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    // OncePerRequestFilter
    @Value("${jwt.secret-key}")
    private String key;

    public static final String REFRESH = "Refresh";

    private final UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            final String token = getToken(request, HttpHeaders.AUTHORIZATION);

            String socialId = JwtTokenUtils.getSocialId(token, key);
            User user = userService.findUser(socialId);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ExpiredJwtException e) {
            log.error("token is expired");
            request.setAttribute("exception", EXPIRED_ACCESS_TOKEN.getCode());
            filterChain.doFilter(request, response);
            return;
        } catch (RuntimeException e) {
            log.error("Error occurs while validating. {}", e.toString());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request, String header) {
        String token = request.getHeader(header);
        if (token != null && token.startsWith("Bearer")) {
            return token.split(" ")[1].trim();
        }
        return null;
    }
}
