package com.linklip.linklipserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linklip.linklipserver.domain.*;
import com.linklip.linklipserver.repository.CategoryRepository;
import com.linklip.linklipserver.repository.ContentRepository;
import com.linklip.linklipserver.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestUtils {

    @Autowired private UserRepository userRepository;
    @Autowired private ContentRepository contentRepository;
    @Autowired private CategoryRepository categoryRepository;

    public User saveUser(String nickname, Social socialType, String socialId) {
        User user =
                User.builder().nickName(nickname).socialType(socialType).socialId(socialId).build();

        userRepository.save(user);

        return user;
    }

    public Content saveLink(String url, String title, String text, Category category, User user) {

        Content content =
                Link.builder()
                        .linkUrl(url)
                        .title(title)
                        .text(text)
                        .category(category)
                        .owner(user)
                        .build();
        contentRepository.save(content);

        return content;
    }

    public Content saveNote(String text, Category category, User user) {

        Content content = Note.builder().text(text).category(category).owner(user).build();
        contentRepository.save(content);

        return content;
    }

    public Category saveCategory(String CategoryName, User user) {

        Category category = Category.builder().name(CategoryName).owner(user).build();
        categoryRepository.save(category);

        return category;
    }

    public String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generateAccessToken(String socialId, String key, long expiredTimesMs) {
        Claims claims = Jwts.claims();
        claims.put("socialId", socialId);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발행시간
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimesMs)) // 만료시간
                .signWith(getKey(key), SignatureAlgorithm.HS256)
                .compact();
    }

    private static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
