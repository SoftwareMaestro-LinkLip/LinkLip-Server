package com.linklip.linklipserver.config.filter;

import static com.linklip.linklipserver.constant.ErrorResponse.EXPIRED_ACCESS_TOKEN;

import com.google.common.net.HttpHeaders;
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
public class JwtTokenFilter extends OncePerRequestFilter {

    private String key;

    public JwtTokenFilter(String key) {
        this.key = key;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            final String token = getToken(request, HttpHeaders.AUTHORIZATION);

            Long userId = JwtTokenUtils.getUserId(token, key);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, null);
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
