package com.linklip.linklipserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.domain.Link;
import com.linklip.linklipserver.domain.Note;
import com.linklip.linklipserver.repository.CategoryRepository;
import com.linklip.linklipserver.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestUtils {

    @Autowired private ContentRepository contentRepository;
    @Autowired private CategoryRepository categoryRepository;

    public Content saveLink(String url, String title, String text, Category category) {

        Content content =
                Link.builder().linkUrl(url).title(title).text(text).category(category).build();
        contentRepository.save(content);

        return content;
    }

    public Content saveNote(String text, Category category) {

        Content content = Note.builder().text(text).category(category).build();
        contentRepository.save(content);

        return content;
    }

    public Category saveCategory(String CategoryName) {

        Category category = Category.builder().name(CategoryName).build();
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
