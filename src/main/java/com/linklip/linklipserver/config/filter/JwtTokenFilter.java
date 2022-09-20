package com.linklip.linklipserver.config.filter;

import static com.linklip.linklipserver.constant.ErrorResponse.EXPIRED_ACCESS_TOKEN;

import com.google.common.net.HttpHeaders;
import com.linklip.linklipserver.config.util.JwtTokenUtils;
import com.linklip.linklipserver.domain.User;
import com.linklip.linklipserver.service.UserService;
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

    private final UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // get header
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            log.error("Error occurs while getting header. header is null or invalid");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = header.split(" ")[1].trim();

            String socialId = JwtTokenUtils.getSocialId(token, key);
            User user = userService.findUser(socialId);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            // TODO
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
}
