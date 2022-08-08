package com.linklip.linklipserver.IntegrationtTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linklip.linklipserver.dto.category.CreateCategoryRequest;
import com.linklip.linklipserver.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CategoryIntegrationTest {

    // @FIXME: 해당 변수에 빨간줄 들어오는데 정확한 이유 파악 필요
    @Autowired private MockMvc mockMvc;

    @Autowired private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("카테고리 생성 통합테스트")
    class createCategoryIntegrationTest {
        @Test
        @DisplayName("일반적인 카테고리 명인 경우 201")
        public void createNormalCategory() throws Exception {
            String name = "취업 공고";
            CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest();
            createCategoryRequest.setName(name);

            mockMvc.perform(
                            post("/category/v1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(createCategoryRequest)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("카테고리 명이 null이라는 문자열인 경우 201")
        public void createStringNullCategory() throws Exception {
            String name = "null";
            CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest();
            createCategoryRequest.setName(name);

            mockMvc.perform(
                            post("/category/v1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(createCategoryRequest)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("카테고리 명이 null인 경우 400")
        public void createNullCategory() throws Exception {
            CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest();
            createCategoryRequest.setName(null);

            mockMvc.perform(
                            post("/category/v1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(createCategoryRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("카테고리 명이 Empty인 경우 400")
        public void createEmtpyCategory() throws Exception {
            CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest();
            createCategoryRequest.setName("");

            mockMvc.perform(
                            post("/category/v1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(createCategoryRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
