package com.linklip.linklipserver.IntegrationtTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.linklip.linklipserver.TestUtils;
import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.domain.Link;
import com.linklip.linklipserver.domain.Note;
import com.linklip.linklipserver.dto.content.UpdateLinkRequest;
import com.linklip.linklipserver.dto.content.note.UpdateNoteRequest;
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
    class FindLinks {

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

            testUtils.saveLink(url1, title, "소프트웨어 마에스트로 12기 연수생 여러분...", category1);
            testUtils.saveLink(url1, null, "소프트웨어 마에스트로 13기 연수생 여러분...", category1);
            testUtils.saveLink(url1, null, null, category2);
            testUtils.saveLink(url2, null, null, category2);
        }

        @Nested
        @DisplayName("카테고리 해당 컨텐츠")
        class FindContentsByCategory {

            @DisplayName("컨텐츠 불러오기")
            @Test
            public void findContent() throws Exception {

                // when
                Long categoryId = category1.getId();
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("categoryId", String.valueOf(categoryId))
                                        .param("page", "0")
                                        .param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.content").value(hasSize(2)));
            }

            @DisplayName("일반적인 검색어")
            @Test
            public void FindContentByNormalTerm() throws Exception {

                // when
                Long categoryId = category1.getId();
                String term = "소마";
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1")
                                        .param("categoryId", String.valueOf(categoryId))
                                        .param("term", term)
                                        .param("page", "0")
                                        .param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.content").value(hasSize(1)));
            }

            @Test
            @DisplayName("검색어에 아무것도 입력하지 않음")
            public void findContentByNullInTerm() throws Exception {

                // when
                Long categoryId = category1.getId();
                String term = "";
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1")
                                        .param("categoryId", String.valueOf(categoryId))
                                        .param("term", term)
                                        .param("page", "0")
                                        .param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.content").value(hasSize(2))); // 모든 Content 출력
            }

            @Test
            @DisplayName("일치하는 검색 결과 없음")
            public void findZeroResult() throws Exception {

                // when
                Long categoryId = category1.getId();
                String term = "1기";
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1")
                                        .param("categoryId", String.valueOf(categoryId))
                                        .param("term", term)
                                        .param("page", "0")
                                        .param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.content").value(hasSize(0))); // 모든 Content 출력
            }
        }

        @Nested
        @DisplayName("전체 검색어")
        class FindContentsByTerm {

            @DisplayName("일반적인 검색어")
            @Test
            public void findContentByNormalTerm() throws Exception {

                // when
                String term = "소마";
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1")
                                        .param("term", term)
                                        .param("page", "0")
                                        .param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.content").value(hasSize(1)));
            }

            @Test
            @DisplayName("검색어에 아무것도 입력하지 않음")
            public void findContentByNullInTerm() throws Exception {

                // when
                String term = "";
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1")
                                        .param("term", term)
                                        .param("page", "0")
                                        .param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.content").value(hasSize(4))); // 모든 Content 출력
            }

            @Test
            @DisplayName("일치하는 검색 결과 없음")
            public void findZeroResult() throws Exception {

                // when
                String term = "1기";
                ResultActions actions =
                        mockMvc.perform(
                                get("/content/v1")
                                        .param("term", term)
                                        .param("page", "0")
                                        .param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.content").value(hasSize(0))); // 모든 Content 출력
            }

            @Test
            @DisplayName("검색어 없이 조회")
            public void findContentByNotTerm() throws Exception {

                // when
                ResultActions actions =
                        mockMvc.perform(get("/content/v1").param("page", "0").param("size", "20"));

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.content").value(hasSize(4))); // 모든 Content 출력
            }
        }

        @Test
        @DisplayName("마지막 페이지 결과 갯수 확인")
        public void getLastPageResult() throws Exception {

            // given
            String baseUrl = "https://www.swmaestro.org";
            String baseTitle = "소프트웨어 마에스트로";
            testUtils.saveLink(baseUrl, baseTitle, "소프트웨어 마에스트로 12기 연수생 여러분...", null);

            // when
            ResultActions actions =
                    mockMvc.perform(get("/content/v1").param("page", "1").param("size", "3"));

            // then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").value(hasSize(2))); // 모든 Content 출력
        }
    }

    @Nested
    @DisplayName("링크 상세보기 통합테스트")
    class FindLink {

        Content link1;
        Content note1;

        @BeforeEach
        public void createContents() {
            // Link
            String url1 = "https://www.swmaestro.org";
            String url2 = "https://www.naver.com";
            String title = "소마";

            Category category1 = Category.builder().name("활동").build();
            Category category2 = Category.builder().name("스펙").build();
            categoryRepository.save(category1);
            categoryRepository.save(category2);

            link1 = testUtils.saveLink(url1, title, "소프트웨어 마에스트로 12기 연수생 여러분...", category1);
            testUtils.saveLink(url1, null, "소프트웨어 마에스트로 12기 연수생 여러분...", category1);
            testUtils.saveLink(url1, null, null, category2);
            testUtils.saveLink(url2, null, null, null);

            // Note
            note1 = testUtils.saveNote("아무 메모나 끄적끄적", category1);
        }

        @Test
        @DisplayName("일반적인 링크 상세보기")
        public void findNormalContent() throws Exception {

            // when
            Long contentId = link1.getId();
            ResultActions actions = mockMvc.perform(get("/content/v1/{contentId}", contentId));

            // then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content.id").value(contentId));
        }

        @Test
        @DisplayName("존재하지 않는 id 상세보기")
        public void findNotExistContent() throws Exception {

            // when
            Long contentId = 987654321L;
            System.out.println(link1.getId());
            ResultActions actions = mockMvc.perform(get("/content/v1/{contentId}", contentId));

            // then
            actions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Note Content 상세보기")
        public void finNoteContent() throws Exception {

            // when
            Long contentId = note1.getId();
            ResultActions actions = mockMvc.perform(get("/content/v1/{contentId}", contentId));

            // then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content.id").value(contentId))
                    .andExpect(jsonPath("$.data.content.text").value(((Note) note1).getText()));
        }

        @Test
        @DisplayName("존재하지 않는 id  Note Content 상세보기")
        public void findNotExistNoteContent() throws Exception {

            // when
            Long contentId = 987654321L;
            ResultActions actions = mockMvc.perform(get("/content/v1/{contentId}", contentId));

            // then
            actions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("링크 수정 통합테스트")
    class UpdateLink {

        @Test
        @DisplayName("링크 컨텐츠의 Title만 수정")
        public void updateTitle() throws Exception {

            Category category = Category.builder().name("활동").build();
            categoryRepository.save(category);

            String url = "https://www.swmaestro.org/sw/main/main.do";
            Content savedContent =
                    testUtils.saveLink(url, "소마", "소프트웨어 마에스트로 13기 연수생 여러분...", category);

            UpdateLinkRequest updateLinkRequest = new UpdateLinkRequest();
            updateLinkRequest.setTitle("소프트웨어 마에스트로");

            ResultActions actions =
                    mockMvc.perform(
                            MockMvcRequestBuilders.patch(
                                            "/content/v1/link/{contentId}", savedContent.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(testUtils.asJsonString(updateLinkRequest)));

            actions.andExpect(status().isOk());

            assertThat(((Link) savedContent).getTitle()).isEqualTo(updateLinkRequest.getTitle());
        }

        @Test
        @DisplayName("링크 컨텐츠의 Title과 카테고리 수정")
        public void updateTitleAndCategory() throws Exception {

            Category category1 = Category.builder().name("활동").build();
            categoryRepository.save(category1);

            Category category2 = Category.builder().name("대외 활동").build();
            categoryRepository.save(category2);

            String url = "https://www.swmaestro.org/sw/main/main.do";
            Content savedContent =
                    testUtils.saveLink(url, "소마", "소프트웨어 마에스트로 13기 연수생 여러분...", category1);

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

            assertThat(((Link) savedContent).getTitle()).isEqualTo(updateLinkRequest.getTitle());
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
            Content savedContent =
                    testUtils.saveLink(url, "소마", "소프트웨어 마에스트로 13기 연수생 여러분...", category1);

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

    @Nested
    @DisplayName("컨텐츠 삭제")
    class DeleteContent {

        @Test
        @DisplayName("일반적인 경우")
        public void deleteNormalContent() throws Exception {

            // given
            Category category = testUtils.saveCategory("포털");
            Content content = testUtils.saveLink("www.naver.com", "네이버", "오늘의 날씨", category);
            boolean isDeleted = content.isDeleted();

            // when
            ResultActions actions =
                    mockMvc.perform(
                            delete("/content/v1/{contentId}", content.getId())
                                    .contentType(MediaType.APPLICATION_JSON));

            // then
            actions.andExpect(status().isOk());

            Content findContent = contentRepository.findById(content.getId()).get();

            assertThat(findContent).isEqualTo(content);
            assertThat(isDeleted).isEqualTo(false);
            assertThat(findContent.isDeleted()).isEqualTo(true);
        }

        @Test
        @DisplayName("존재하지 않는 컨텐츠 삭제")
        public void deleteNotExistContent() throws Exception {

            // given
            Category category = testUtils.saveCategory("포털");
            testUtils.saveLink("www.naver.com", "네이버", "오늘의 날씨", category);

            // when
            Long contentId = 987654321L;
            ResultActions actions =
                    mockMvc.perform(
                            delete("/content/v1/{contentId}", contentId)
                                    .contentType(MediaType.APPLICATION_JSON));

            // then
            actions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("메모 검색 통합테스트")
    class FindNotes {
        Category category1;
        Category category2;
        Content note1;

        @BeforeEach
        public void createContents() {
            String text1 = "9월 11일에 소망팀 모의면접 (OS, DB)";
            String text2 = "디자이너 외주 마감일자 확정 필요";
            String text3 = "갑자기 무슨 태풍이래";
            String text4 = "다음주 멘토링은 목요일에 온라인으로";
            String text5 = "모의면접 방향성 고민해보자!";

            category1 = Category.builder().name("활동").build();
            category2 = Category.builder().name("프로젝트").build();
            categoryRepository.save(category1);
            categoryRepository.save(category2);

            note1 = testUtils.saveNote(text1, category1);
            testUtils.saveNote(text2, category2);
            testUtils.saveNote(text3, null);
            testUtils.saveNote(text4, category1);
            testUtils.saveNote(text5, null);
        }

        @DisplayName("전체 메모 불러오기")
        @Test
        public void findAllContent() throws Exception {
            ResultActions actions =
                    mockMvc.perform(
                            get("/content/v1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .param("page", "0")
                                    .param("size", "20"));

            // then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").value(hasSize(5)));
        }

        @DisplayName("검색어 없이 카테고리로 조회")
        @Test
        public void findContentWithCategory() throws Exception {

            // when
            Long categoryId = category1.getId();
            ResultActions actions =
                    mockMvc.perform(
                            get("/content/v1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .param("categoryId", String.valueOf(categoryId))
                                    .param("page", "0")
                                    .param("size", "20"));

            // then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").value(hasSize(2)));
        }

        @DisplayName("검색어와 함께 카테고리로 조회")
        @Test
        public void findContentWithCategoryAndTerm() throws Exception {

            // when
            Long categoryId1 = category1.getId();
            Long categoryId2 = category2.getId();
            String term = "면접";

            ResultActions actions1 =
                    mockMvc.perform(
                            get("/content/v1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .param("categoryId", String.valueOf(categoryId1))
                                    .param("term", String.valueOf(term))
                                    .param("page", "0")
                                    .param("size", "20"));

            ResultActions actions2 =
                    mockMvc.perform(
                            get("/content/v1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .param("categoryId", String.valueOf(categoryId2))
                                    .param("term", String.valueOf(term))
                                    .param("page", "0")
                                    .param("size", "20"));

            // then
            actions1.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").value(hasSize(1)));

            actions2.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").value(hasSize(0)));
        }

        @DisplayName("카테고리 없이 검색어로 조회")
        @Test
        public void findContentWithTerm() throws Exception {

            // when
            String term = "면접";

            ResultActions actions =
                    mockMvc.perform(
                            get("/content/v1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .param("term", String.valueOf(term))
                                    .param("page", "0")
                                    .param("size", "20"));

            // then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").value(hasSize(2)));
        }

        @DisplayName("메모 상세 조회")
        @Test
        public void findContentDetail() throws Exception {

            // when
            Long contentId = note1.getId();

            ResultActions actions =
                    mockMvc.perform(
                            get("/content/v1/{contentId}", contentId)
                                    .contentType(MediaType.APPLICATION_JSON));

            // then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content.id").value(contentId))
                    .andExpect(jsonPath("$.data.content.text").value(((Note) note1).getText()));
        }
    }

    @Nested
    @DisplayName("메모 수정 통합테스트")
    class UpdateNoteContent {

        @Test
        @DisplayName("메모 컨텐츠 text 수정")
        public void updateText() throws Exception {

            // given
            Category category = testUtils.saveCategory("ToDo List");

            String text = "마트에서 사과 구매";
            Content savedContent = testUtils.saveNote(text, category);

            UpdateNoteRequest updateNoteRequest = new UpdateNoteRequest();
            updateNoteRequest.setText("마트에서 포도 구매");

            // when
            ResultActions actions =
                    mockMvc.perform(
                            MockMvcRequestBuilders.patch(
                                            "/content/v1/note/{contentId}", savedContent.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(testUtils.asJsonString(updateNoteRequest)));

            // then
            actions.andExpect(status().isOk());
            assertThat(((Note) savedContent).getText()).isEqualTo(updateNoteRequest.getText());
        }

        @Test
        @DisplayName("메모 컨텐츠 text, 카테고리 수정")
        public void updateTextAndCategory() throws Exception {

            // given
            Category fromCategory = testUtils.saveCategory("ToDo List");
            Category toCategory = testUtils.saveCategory("ToDo");

            String text = "마트에서 사과 구매";
            Content savedContent = testUtils.saveNote(text, fromCategory);

            UpdateNoteRequest updateNoteRequest = new UpdateNoteRequest();
            updateNoteRequest.setText("마트에서 포도 구매");
            updateNoteRequest.setCategoryId(toCategory.getId());

            // when
            ResultActions actions =
                    mockMvc.perform(
                            MockMvcRequestBuilders.patch(
                                            "/content/v1/note/{contentId}", savedContent.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(testUtils.asJsonString(updateNoteRequest)));

            // then
            actions.andExpect(status().isOk());
            assertThat(((Note) savedContent).getText()).isEqualTo(updateNoteRequest.getText());
            assertThat(savedContent.getCategory().getId())
                    .isEqualTo(updateNoteRequest.getCategoryId());
        }

        @Test
        @DisplayName("메모 컨텐츠의 text 없이 수정 요청을 보내면 400")
        public void updateWithoutText() throws Exception {

            // given
            Category fromCategory = testUtils.saveCategory("ToDo List");
            Category toCategory = testUtils.saveCategory("ToDo");

            String text = "마트에서 사과 구매";
            Content savedContent = testUtils.saveNote(text, fromCategory);

            UpdateNoteRequest updateNoteRequest = new UpdateNoteRequest();
            updateNoteRequest.setText("");
            updateNoteRequest.setCategoryId(toCategory.getId());

            // when
            ResultActions actions =
                    mockMvc.perform(
                            MockMvcRequestBuilders.patch(
                                            "/content/v1/note/{contentId}", savedContent.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(testUtils.asJsonString(updateNoteRequest)));

            // then
            actions.andExpect(status().isBadRequest());
        }
    }
}
