package com.linklip.linklipserver.IntegrationtTest;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.repository.CategoryRepository;
import com.linklip.linklipserver.repository.ContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ContentIntegrationTest {

    @Autowired MockMvc mockMvc; // 해당 변수에 빨간줄 들어오는데 정확한 이유 파악 필요

    @Autowired private ContentRepository contentRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("링크 검색 통합테스트")
    class findContents {

        Category category1;

        @BeforeEach
        public void createContents() {
            String url1 = "https://www.swmaestro.org";
            String url2 = "https://www.naver.com";
            String title = "소마";

            category1 = Category.builder().name("활동").build();
            Category category2 = Category.builder().name("스펙").build();
            categoryRepository.save(category1);
            categoryRepository.save(category2);

            saveContent(url1, title, "소프트웨어 마에스트로 12기 연수생 여러분...", category1);
            saveContent(url1, null, "소프트웨어 마에스트로 13기 연수생 여러분...", category1);
            saveContent(url1, null, null, category2);
            saveContent(url2, null, null, category2);
        }

        @Nested
        @DisplayName("카테고리 해당 컨텐츠")
        class findContentsByCategory {

            @DisplayName("컨텐츠 불러오기")
            @Test
            public void findContent() throws Exception {

                // when
                Long categoryId = category1.getId();
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1/link")
                                        .param("categoryId", String.valueOf(categoryId))
                                        .param("page", "0")
                                        .param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.pageDto.content").value(hasSize(2)));
            }

            @DisplayName("일반적인 검색어")
            @Test
            public void findContentByNormalTerm() throws Exception {

                // when
                Long categoryId = category1.getId();
                String term = "소마";
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1/link")
                                        .param("categoryId", String.valueOf(categoryId))
                                        .param("term", term)
                                        .param("page", "0")
                                        .param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.pageDto.content").value(hasSize(1)));
            }

            @Test
            @DisplayName("검색어에 아무것도 입력하지 않음")
            public void findContentByNullInTerm() throws Exception {

                // when
                Long categoryId = category1.getId();
                String term = "";
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1/link")
                                        .param("categoryId", String.valueOf(categoryId))
                                        .param("term", term)
                                        .param("page", "0")
                                        .param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(
                                jsonPath("$.data.pageDto.content")
                                        .value(hasSize(2))); // 모든 Content 출력
            }

            @Test
            @DisplayName("일치하는 검색 결과 없음")
            public void findZeroResult() throws Exception {

                // when
                Long categoryId = category1.getId();
                String term = "1기";
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1/link")
                                        .param("categoryId", String.valueOf(categoryId))
                                        .param("term", term)
                                        .param("page", "0")
                                        .param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(
                                jsonPath("$.data.pageDto.content")
                                        .value(hasSize(0))); // 모든 Content 출력
            }
        }

        @Nested
        @DisplayName("전체 검색어")
        class findContentsByTerm {

            @DisplayName("일반적인 검색어")
            @Test
            public void findContentByNormalTerm() throws Exception {

                // when
                String term = "소마";
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1/link")
                                        .param("term", term)
                                        .param("page", "0")
                                        .param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.pageDto.content").value(hasSize(1)));
            }

            @Test
            @DisplayName("검색어에 아무것도 입력하지 않음")
            public void findContentByNullInTerm() throws Exception {

                // when
                String term = "";
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1/link")
                                        .param("term", term)
                                        .param("page", "0")
                                        .param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(
                                jsonPath("$.data.pageDto.content")
                                        .value(hasSize(4))); // 모든 Content 출력
            }

            @Test
            @DisplayName("일치하는 검색 결과 없음")
            public void findZeroResult() throws Exception {

                // when
                String term = "1기";
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1/link")
                                        .param("term", term)
                                        .param("page", "0")
                                        .param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(
                                jsonPath("$.data.pageDto.content")
                                        .value(hasSize(0))); // 모든 Content 출력
            }

            @Test
            @DisplayName("검색어 없이 조회")
            public void findContentByNotTerm() throws Exception {

                // when
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1/link").param("page", "0").param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(
                                jsonPath("$.data.pageDto.content")
                                        .value(hasSize(4))); // 모든 Content 출력
            }
        }

        @Test
        @DisplayName("마지막 페이지 결과 갯수 확인")
        public void getLastPageResult() throws Exception {

            // given
            String baseUrl = "https://www.swmaestro.org";
            String baseTitle = "소프트웨어 마에스트로";
            saveContent(baseUrl, baseTitle, "소프트웨어 마에스트로 12기 연수생 여러분...", category1);

            // when
            ResultActions actions =
                    mockMvc.perform(get("/content/v1/link").param("page", "1").param("size", "3"));

            // then
            actions.andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.data.pageDto.content").value(hasSize(2))); // 모든 Content 출력
        }
    }

    @Nested
    @DisplayName("링크 상세보기 통합테스트")
    class findContent {

        Content content;

        @BeforeEach
        public void createContents() {
            String url1 = "https://www.swmaestro.org";
            String url2 = "https://www.naver.com";
            String title = "소마";

            Category category1 = Category.builder().name("활동").build();
            Category category2 = Category.builder().name("스펙").build();
            categoryRepository.save(category1);
            categoryRepository.save(category2);

            content = saveAndReturnContent(url1, title, "소프트웨어 마에스트로 12기 연수생 여러분...", category1);
            saveContent(url1, null, "소프트웨어 마에스트로 12기 연수생 여러분...", category1);
            saveContent(url1, null, null, category2);
            saveContent(url2, null, null, null);
        }

        @Test
        @DisplayName("일반적인 링크 상세보기")
        public void findNormalContent() throws Exception {

            // when
            Long contentId = content.getId();
            ResultActions actions = mockMvc.perform(get("/content/v1/link/{contentId}", contentId));

            // then
            actions.andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(contentId));
        }

        @Test
        @DisplayName("존재하지 않는 id 상세보기")
        public void findNotExistContent() throws Exception {

            // when
            Long contentId = 987654321L;
            System.out.println(content.getId());
            ResultActions actions = mockMvc.perform(get("/content/v1/link/{contentId}", contentId));

            // then
            actions.andExpect(status().isBadRequest());
        }
    }

    public void saveContent(String url, String title, String text, Category category) {

        Content content =
                Content.builder().linkUrl(url).title(title).text(text).category(category).build();
        contentRepository.save(content);
    }

    public Content saveAndReturnContent(String url, String title, String text, Category category) {

        Content content =
                Content.builder().linkUrl(url).title(title).text(text).category(category).build();
        return contentRepository.save(content);
    }
}
