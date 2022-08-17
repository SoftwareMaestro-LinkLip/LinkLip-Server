package com.linklip.linklipserver.IntegrationtTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.linklip.linklipserver.TestUtils;
import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.dto.category.CreateCategoryRequest;
import com.linklip.linklipserver.dto.category.UpdateCategoryRequest;
import com.linklip.linklipserver.repository.CategoryRepository;
import com.linklip.linklipserver.repository.ContentRepository;
import java.util.List;
import java.util.Optional;
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
    @Autowired private ContentRepository contentRepository;

    @Autowired private TestUtils testUtils;

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
                                    .content(testUtils.asJsonString(createCategoryRequest)))
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
                                    .content(testUtils.asJsonString(createCategoryRequest)))
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
                                    .content(testUtils.asJsonString(createCategoryRequest)))
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
                                    .content(testUtils.asJsonString(createCategoryRequest)))
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
                            patch("/category/v1/{categoryId}", categoryId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(testUtils.asJsonString(updateCategoryRequest)));

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
                            patch("/category/v1/{categoryId}", categoryId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(testUtils.asJsonString(updateCategoryRequest)));

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
                            patch("/category/v1/{categoryId}", categoryId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(testUtils.asJsonString(updateCategoryRequest)));

            // then
            actions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("카테고리 삭제 통합테스트")
    class deleteCategoryIntegrationTest {
        @Test
        @DisplayName("컨텐츠가 존재하지 않는 카테고리 수정")
        public void deleteEmptyCategory() throws Exception {
            // given
            Category category1 = Category.builder().name("운동").build();
            Category category2 = Category.builder().name("개발").build();
            Category savedCategory1 = categoryRepository.save(category1);
            Category savedCategory2 = categoryRepository.save(category2);

            // when
            ResultActions actions =
                    mockMvc.perform(
                            delete("/category/v1/{categoryId}", savedCategory1.getId())
                                    .contentType(MediaType.APPLICATION_JSON));

            // then
            actions.andExpect(status().isOk());

            List<Category> all = categoryRepository.findAll();
            assertThat(all.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("컨텐츠가 존재하는 카테고리 수정")
        public void deleteCategoryHavingContents() throws Exception {
            // given
            Category category1 = Category.builder().name("홈페이지").build();
            Category savedCategory1 = categoryRepository.save(category1);
            Category category2 = Category.builder().name("개발").build();
            Category savedCategory2 = categoryRepository.save(category2);

            Content content1 =
                    testUtils.saveContent("naver.com", "Naver", "뉴스 로그인 회원가입", savedCategory1);
            Content content2 =
                    testUtils.saveContent("soma.org", "소프트웨어 마에스트로", "소마 홈페이지", savedCategory1);
            Content content3 =
                    testUtils.saveContent("soma.org", "소프트웨어 마에스트로", "소마 홈페이지", savedCategory2);

            // when
            ResultActions actions =
                    mockMvc.perform(
                            delete("/category/v1/{categoryId}", savedCategory1.getId())
                                    .contentType(MediaType.APPLICATION_JSON));

            // then
            actions.andExpect(status().isOk());

            List<Category> all = categoryRepository.findAll();
            assertThat(all.size()).isEqualTo(1);

            Optional<Content> findContent1 = contentRepository.findById(content1.getId());
            Optional<Content> findContent2 = contentRepository.findById(content2.getId());
            Optional<Content> findContent3 = contentRepository.findById(content3.getId());

            assertThat(findContent1.get().getCategory()).isEqualTo(null);
            assertThat(findContent2.get().getCategory()).isEqualTo(null);
            assertThat(findContent3.get().getCategory().getId()).isEqualTo(savedCategory2.getId());
        }
    }
}
