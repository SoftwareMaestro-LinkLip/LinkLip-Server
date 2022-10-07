package com.linklip.linklipserver.config;

import com.linklip.linklipserver.config.filter.JwtTokenFilter;
import com.linklip.linklipserver.config.oauth.OAuth2SuccessHandler;
import com.linklip.linklipserver.config.oauth.OAuth2UserService;
import com.linklip.linklipserver.service.UserService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// TODO 불평하는 원인 파악 필요
@SuppressFBWarnings("EI_EXPOSE_REP2")
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final OAuth2UserService oauth2UserService;
    private final OAuth2SuccessHandler oauth2SuccessHandler;

    @Value("${jwt.secret-key}")
    private String key;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/content/**", "/category/**")
                .authenticated()
                .and()
                .oauth2Login() // OAuth2 로그인 설정 시작점
                .userInfoEndpoint() // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때 설정 담당
                .userService(
                        oauth2UserService) // OAuth2 로그인 성공 시, 후작업을 진행할 UserService 인터페이스 구현체 등록
                .and()
                .successHandler(oauth2SuccessHandler)
                .and()
                .addFilterBefore(
                        new JwtTokenFilter(userService, key),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/token/**", "/ping")
                .antMatchers(HttpMethod.OPTIONS, "/category/v1", "/content/v1");
    }
}
