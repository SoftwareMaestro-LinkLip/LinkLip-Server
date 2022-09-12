package com.linklip.linklipserver.config;

import static com.linklip.linklipserver.constant.ErrorResponse.INVALID_TOKEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationEntryPoint
        implements org.springframework.security.web.AuthenticationEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", INVALID_TOKEN.getStatus());
        body.put("success", INVALID_TOKEN.getSuccess());
        body.put("message", INVALID_TOKEN.getMessage());

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(INVALID_TOKEN.getStatus());
        String result = objectMapper.writeValueAsString(body);
        response.getWriter().write(result);
    }
}
