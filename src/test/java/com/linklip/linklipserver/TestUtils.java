package com.linklip.linklipserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linklip.linklipserver.domain.*;
import com.linklip.linklipserver.repository.CategoryRepository;
import com.linklip.linklipserver.repository.ContentRepository;
import com.linklip.linklipserver.repository.UserRepository;
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
}
