package com.linklip.linklipserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestUtils {

    @Autowired private ContentRepository contentRepository;

    public Content saveContent(String url, String title, String text, Category category) {

        Content content =
                Content.builder().linkUrl(url).title(title).text(text).category(category).build();
        contentRepository.save(content);

        return content;
    }

    public String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
