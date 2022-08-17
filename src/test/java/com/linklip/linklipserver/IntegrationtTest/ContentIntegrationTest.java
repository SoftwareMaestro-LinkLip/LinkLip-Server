package com.linklip.linklipserver.IntegrationtTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.linklip.linklipserver.TestUtils;
import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.dto.content.UpdateLinkRequest;
import com.linklip.linklipserver.repository.CategoryRepository;
import com.linklip.linklipserver.repository.ContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ContentIntegrationTest {

    @Autowired MockMvc mockMvc; // 해당 변수에 빨간줄 들어오는데 정확한 이유 파악 필요

    @Autowired private ContentRepository contentRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Autowired private TestUtils testUtils;

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

    public Content saveContent(String url, String title, String text, Category category) {

        Content content =
                Content.builder().linkUrl(url).title(title).text(text).category(category).build();
        contentRepository.save(content);

        return content;
    }

    @Nested
    @DisplayName("링크 수정 통합테스트")
    class updateContent {

        @Test
        @DisplayName("링크 컨텐츠의 Title만 수정")
        public void updateTitle() throws Exception {

            Category category = Category.builder().name("활동").build();
            categoryRepository.save(category);

            String url = "https://www.swmaestro.org/sw/main/main.do";
            Content savedContent = saveContent(url, "소마", "소프트웨어 마에스트로 13기 연수생 여러분...", category);

            UpdateLinkRequest updateLinkRequest = new UpdateLinkRequest();
            updateLinkRequest.setTitle("소프트웨어 마에스트로");

            ResultActions actions =
                    mockMvc.perform(
                            MockMvcRequestBuilders.patch(
                                            "/content/v1/link/{contentId}", savedContent.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(testUtils.asJsonString(updateLinkRequest)));

            actions.andExpect(status().isOk());

            assertThat(savedContent.getTitle()).isEqualTo(updateLinkRequest.getTitle());
        }

        @Test
        @DisplayName("링크 컨텐츠의 Title과 카테고리 수정")
        public void updateTitleAndCategory() throws Exception {

            Category category1 = Category.builder().name("활동").build();
            categoryRepository.save(category1);

            Category category2 = Category.builder().name("대외 활동").build();
            categoryRepository.save(category2);

            String url = "https://www.swmaestro.org/sw/main/main.do";
            Content savedContent = saveContent(url, "소마", "소프트웨어 마에스트로 13기 연수생 여러분...", category1);

            UpdateLinkRequest updateLinkRequest = new UpdateLinkRequest();
            updateLinkRequest.setTitle("소프트웨어 마에스트로");
            updateLinkRequest.setCategoryId(category2.getId());

            ResultActions actions =
                    mockMvc.perform(
                            MockMvcRequestBuilders.patch(
                                            "/content/v1/link/{contentId}", savedContent.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(testUtils.asJsonString(updateLinkRequest)));

            actions.andExpect(status().isOk());

            assertThat(savedContent.getTitle()).isEqualTo(updateLinkRequest.getTitle());
            assertThat(savedContent.getCategory().getId())
                    .isEqualTo(updateLinkRequest.getCategoryId());
        }

        @Test
        @DisplayName("링크 컨텐츠의 Title 없이 수정 요청을 보내면 400")
        public void updateWithoutTitle() throws Exception {

            Category category1 = Category.builder().name("활동").build();
            categoryRepository.save(category1);

            Category category2 = Category.builder().name("대외 활동").build();
            categoryRepository.save(category2);

            String url = "https://www.swmaestro.org/sw/main/main.do";
            Content savedContent = saveContent(url, "소마", "소프트웨어 마에스트로 13기 연수생 여러분...", category1);

            UpdateLinkRequest updateLinkRequest = new UpdateLinkRequest();
            updateLinkRequest.setCategoryId(category2.getId());

            ResultActions actions =
                    mockMvc.perform(
                            MockMvcRequestBuilders.patch(
                                            "/content/v1/link/{contentId}", savedContent.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(testUtils.asJsonString(updateLinkRequest)));

            actions.andExpect(status().isBadRequest());
        }
    }
}
