package com.linklip.linklipserver.config;

import static com.linklip.linklipserver.constant.ErrorResponse.EXPIRED_ACCESS_TOKEN;
import static com.linklip.linklipserver.constant.ErrorResponse.INVALID_TOKEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linklip.linklipserver.constant.ErrorResponse;
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

        String exception = (String) request.getAttribute("exception");
        if (exception.equals(EXPIRED_ACCESS_TOKEN.getCode())) {
            setResponse(response, EXPIRED_ACCESS_TOKEN);
            return;
        }

        setResponse(response, INVALID_TOKEN);
    }

    private void setResponse(HttpServletResponse response, ErrorResponse error) throws IOException {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("status", error.getStatus());
        body.put("success", error.getSuccess());
        body.put("message", error.getMessage());

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(error.getStatus());
        String result = objectMapper.writeValueAsString(body);
        response.getWriter().write(result);
    }
}
