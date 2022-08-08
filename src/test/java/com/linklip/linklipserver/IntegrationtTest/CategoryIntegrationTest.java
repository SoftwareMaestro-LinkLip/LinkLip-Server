package com.linklip.linklipserver.IntegrationtTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.dto.category.CreateCategoryRequest;
import com.linklip.linklipserver.dto.category.UpdateCategoryRequest;
import com.linklip.linklipserver.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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

    @Nested
    @DisplayName("카테고리 조회 통합테스트")
    class findCategoryIntegrationTest {
        @Test
        @DisplayName("카테고리 조회에 성공한 경우 200")
        public void findCategory() throws Exception {
            mockMvc.perform(get("/category/v1").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("카테고리 수정 통합테스트")
    class updateCategoryIntegrationTest {
        @Test
        @DisplayName("일반적인 카테고리명 수정")
        public void updateCategory() throws Exception {

            // given
            Category category = Category.builder().name("운동").build();
            Category entity = categoryRepository.save(category);
            long categoryId = entity.getId();

            // when
            UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
            updateCategoryRequest.setName("스포츠");

            ResultActions actions =
                    mockMvc.perform(
                            patch("/category/v1/{id}", categoryId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(updateCategoryRequest)));

            // then
            actions.andExpect(status().isOk()).andDo(print());
        }

        @Test
        @DisplayName("이미 존재하는 카테고리명으로 수정")
        public void updateCategoryByPresentName() throws Exception {

            // given
            Category category1 = Category.builder().name("운동").build();
            Category entity = categoryRepository.save(category1);
            Category category2 = Category.builder().name("스포츠").build();
            categoryRepository.save(category2);
            long categoryId = entity.getId();

            // when
            UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
            updateCategoryRequest.setName("스포츠");

            ResultActions actions =
                    mockMvc.perform(
                            patch("/category/v1/{id}", categoryId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(updateCategoryRequest)));

            // then
            actions.andExpect(status().isOk());
        }

        @Test
        @DisplayName("빈값으로 카테고리명 수정")
        public void updateCategoryByNullName() throws Exception {

            // given
            Category category = Category.builder().name("운동").build();
            Category entity = categoryRepository.save(category);
            long categoryId = entity.getId();

            // when
            UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
            updateCategoryRequest.setName("");

            ResultActions actions =
                    mockMvc.perform(
                            patch("/category/v1/{id}", categoryId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(updateCategoryRequest)));

            // then
            actions.andExpect(status().isBadRequest());
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
