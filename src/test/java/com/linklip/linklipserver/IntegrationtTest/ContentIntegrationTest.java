package com.linklip.linklipserver.IntegrationtTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.linklip.linklipserver.TestUtils;
import com.linklip.linklipserver.domain.*;
import com.linklip.linklipserver.dto.content.UpdateLinkRequest;
import com.linklip.linklipserver.dto.content.note.UpdateNoteRequest;
import com.linklip.linklipserver.repository.ContentRepository;
import com.linklip.linklipserver.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired MockMvc mockMvc;

    @Autowired private UserRepository userRepository;
    @Autowired private ContentRepository contentRepository;

    @Autowired private TestUtils testUtils;

    @Value("${jwt.secret-key}")
    private String key;

    @Value("${jwt.token-expired-time-ms}")
    private Long expiredTime;

    User testUser;
    String accessToken;

    @Nested
    @DisplayName("링크 검색 통합테스트")
    class FindLinks {

        Category category1;

        @BeforeEach
        public void createContents() {

            String socialId = "GOOGLE_123123123";
            testUser =
                    User.builder()
                            .nickName("Team LinkLip")
                            .socialId("GOOGLE_123123123")
                            .socialType(Social.GOOGLE)
                            .build();
            userRepository.save(testUser);

            accessToken = generateAccessToken(socialId, key, expiredTime);

            String url1 = "https://www.swmaestro.org";
            String url2 = "https://www.naver.com";
            String title = "소마";
            category1 = testUtils.saveCategory("활동", testUser);
            Category category2 = testUtils.saveCategory("스펙", testUser);

            testUtils.saveLink(url1, title, "소프트웨어 마에스트로 12기 연수생 여러분...", category1, testUser);
            testUtils.saveLink(url1, null, "소프트웨어 마에스트로 13기 연수생 여러분...", category1, testUser);
            testUtils.saveLink(url1, null, null, category2, testUser);
            testUtils.saveLink(url2, null, null, category2, testUser);
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
                                        .header("Authorization", "Bearer " + accessToken)
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
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + accessToken)
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
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + accessToken)
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
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + accessToken)
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
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + accessToken)
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
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + accessToken)
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
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + accessToken)
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
                        mockMvc.perform(
                                get("/content/v1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + accessToken)
                                        .param("page", "0")
                                        .param("size", "20"));

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
            testUtils.saveLink(baseUrl, baseTitle, "소프트웨어 마에스트로 12기 연수생 여러분...", null, testUser);

            // when
            ResultActions actions =
                    mockMvc.perform(
                            get("/content/v1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken)
                                    .param("page", "1")
                                    .param("size", "3"));

            // then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").value(hasSize(2))); // 모든 Content 출력
        }
    }

    @Nested
    @DisplayName("링크 상세보기 통합테스트")
    class FindLink {

        Content link1;

        @BeforeEach
        public void createContents() {

            String socialId = "GOOGLE_123123123";
            testUser =
                    User.builder()
                            .nickName("Team LinkLip")
                            .socialId("GOOGLE_123123123")
                            .socialType(Social.GOOGLE)
                            .build();
            userRepository.save(testUser);

            accessToken =
                    generateAccessToken(
                            socialId,
                            "software-maestro-13.linklip-application-2022.secret-key",
                            7200000);

            // Link
            String url1 = "https://www.swmaestro.org";
            String url2 = "https://www.naver.com";
            String title = "소마";

            Category category1 = testUtils.saveCategory("활동", testUser);
            Category category2 = testUtils.saveCategory("스펙", testUser);

            link1 =
                    testUtils.saveLink(
                            url1, title, "소프트웨어 마에스트로 12기 연수생 여러분...", category1, testUser);
            testUtils.saveLink(url1, null, "소프트웨어 마에스트로 12기 연수생 여러분...", category1, testUser);
            testUtils.saveLink(url1, null, null, category2, testUser);
            testUtils.saveLink(url2, null, null, null, testUser);
        }

        @Test
        @DisplayName("일반적인 링크 상세보기")
        public void findNormalContent() throws Exception {

            // when
            Long contentId = link1.getId();
            ResultActions actions =
                    mockMvc.perform(
                            get("/content/v1/{contentId}", contentId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken));

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
            ResultActions actions =
                    mockMvc.perform(
                            get("/content/v1/{contentId}", contentId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken));

            // then
            actions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("링크 수정 통합테스트")
    class UpdateLink {

        @BeforeEach
        public void createUser() {
            String socialId = "GOOGLE_123123123";
            testUser =
                    User.builder()
                            .nickName("Team LinkLip")
                            .socialId("GOOGLE_123123123")
                            .socialType(Social.GOOGLE)
                            .build();
            userRepository.save(testUser);

            accessToken =
                    generateAccessToken(
                            socialId,
                            "software-maestro-13.linklip-application-2022.secret-key",
                            7200000);
        }

        @Test
        @DisplayName("링크 컨텐츠의 Title만 수정")
        public void updateTitle() throws Exception {

            Category category = testUtils.saveCategory("활동", testUser);

            String url = "https://www.swmaestro.org/sw/main/main.do";
            Content savedContent =
                    testUtils.saveLink(url, "소마", "소프트웨어 마에스트로 13기 연수생 여러분...", category, testUser);

            UpdateLinkRequest updateLinkRequest = new UpdateLinkRequest();
            updateLinkRequest.setTitle("소프트웨어 마에스트로");

            ResultActions actions =
                    mockMvc.perform(
                            MockMvcRequestBuilders.patch(
                                            "/content/v1/link/{contentId}", savedContent.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken)
                                    .content(testUtils.asJsonString(updateLinkRequest)));

            actions.andExpect(status().isOk());

            assertThat(((Link) savedContent).getTitle()).isEqualTo(updateLinkRequest.getTitle());
        }

        @Test
        @DisplayName("링크 컨텐츠의 Title과 카테고리 수정")
        public void updateTitleAndCategory() throws Exception {

            Category category1 = testUtils.saveCategory("활동", testUser);

            Category category2 = testUtils.saveCategory("대외 활동", testUser);

            String url = "https://www.swmaestro.org/sw/main/main.do";
            Content savedContent =
                    testUtils.saveLink(
                            url, "소마", "소프트웨어 마에스트로 13기 연수생 여러분...", category1, testUser);

            UpdateLinkRequest updateLinkRequest = new UpdateLinkRequest();
            updateLinkRequest.setTitle("소프트웨어 마에스트로");
            updateLinkRequest.setCategoryId(category2.getId());

            ResultActions actions =
                    mockMvc.perform(
                            MockMvcRequestBuilders.patch(
                                            "/content/v1/link/{contentId}", savedContent.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken)
                                    .content(testUtils.asJsonString(updateLinkRequest)));

            actions.andExpect(status().isOk());

            assertThat(((Link) savedContent).getTitle()).isEqualTo(updateLinkRequest.getTitle());
            assertThat(savedContent.getCategory().getId())
                    .isEqualTo(updateLinkRequest.getCategoryId());
        }

        @Test
        @DisplayName("링크 컨텐츠의 Title 없이 수정 요청을 보내면 400")
        public void updateWithoutTitle() throws Exception {

            Category category1 = testUtils.saveCategory("활동", testUser);

            Category category2 = testUtils.saveCategory("대외 활동", testUser);

            String url = "https://www.swmaestro.org/sw/main/main.do";
            Content savedContent =
                    testUtils.saveLink(
                            url, "소마", "소프트웨어 마에스트로 13기 연수생 여러분...", category1, testUser);

            UpdateLinkRequest updateLinkRequest = new UpdateLinkRequest();
            updateLinkRequest.setCategoryId(category2.getId());

            ResultActions actions =
                    mockMvc.perform(
                            MockMvcRequestBuilders.patch(
                                            "/content/v1/link/{contentId}", savedContent.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken)
                                    .content(testUtils.asJsonString(updateLinkRequest)));

            actions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("컨텐츠 삭제")
    class DeleteContent {

        @BeforeEach
        public void createUser() {

            String socialId = "GOOGLE_123123123";
            testUser =
                    User.builder()
                            .nickName("Team LinkLip")
                            .socialId("GOOGLE_123123123")
                            .socialType(Social.GOOGLE)
                            .build();
            userRepository.save(testUser);

            accessToken =
                    generateAccessToken(
                            socialId,
                            "software-maestro-13.linklip-application-2022.secret-key",
                            7200000);
        }

        @Test
        @DisplayName("일반적인 경우")
        public void deleteNormalContent() throws Exception {

            // given
            Category category = testUtils.saveCategory("포털", testUser);
            Content content =
                    testUtils.saveLink("www.naver.com", "네이버", "오늘의 날씨", category, testUser);
            boolean isDeleted = content.isDeleted();

            // when
            ResultActions actions =
                    mockMvc.perform(
                            delete("/content/v1/{contentId}", content.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken));

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
            Category category = testUtils.saveCategory("포털", testUser);
            testUtils.saveLink("www.naver.com", "네이버", "오늘의 날씨", category, testUser);

            // when
            Long contentId = 987654321L;
            ResultActions actions =
                    mockMvc.perform(
                            delete("/content/v1/{contentId}", contentId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken));

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

            String socialId = "GOOGLE_123123123";
            testUser =
                    User.builder()
                            .nickName("Team LinkLip")
                            .socialId("GOOGLE_123123123")
                            .socialType(Social.GOOGLE)
                            .build();
            userRepository.save(testUser);

            accessToken =
                    generateAccessToken(
                            socialId,
                            "software-maestro-13.linklip-application-2022.secret-key",
                            7200000);

            String text1 = "9월 11일에 소망팀 모의면접 (OS, DB)";
            String text2 = "디자이너 외주 마감일자 확정 필요";
            String text3 = "갑자기 무슨 태풍이래";
            String text4 = "다음주 멘토링은 목요일에 온라인으로";
            String text5 = "모의면접 방향성 고민해보자!";

            category1 = testUtils.saveCategory("활동", testUser);
            category2 = testUtils.saveCategory("프로젝트", testUser);

            note1 = testUtils.saveNote(text1, category1, testUser);
            testUtils.saveNote(text2, category2, testUser);
            testUtils.saveNote(text3, null, testUser);
            testUtils.saveNote(text4, category1, testUser);
            testUtils.saveNote(text5, null, testUser);
        }

        @DisplayName("전체 메모 불러오기")
        @Test
        public void findAllContent() throws Exception {
            ResultActions actions =
                    mockMvc.perform(
                            get("/content/v1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken)
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
                                    .header("Authorization", "Bearer " + accessToken)
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
                                    .header("Authorization", "Bearer " + accessToken)
                                    .param("categoryId", String.valueOf(categoryId1))
                                    .param("term", String.valueOf(term))
                                    .param("page", "0")
                                    .param("size", "20"));

            ResultActions actions2 =
                    mockMvc.perform(
                            get("/content/v1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken)
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
                                    .header("Authorization", "Bearer " + accessToken)
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
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken));
            // then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content.id").value(contentId))
                    .andExpect(jsonPath("$.data.content.text").value(((Note) note1).getText()));
        }
    }

    @Nested
    @DisplayName("메모 상세보기 통합테스트")
    class FindNote {

        Content note1;

        @BeforeEach
        public void createContents() {

            String socialId = "GOOGLE_123123123";
            testUser =
                    User.builder()
                            .nickName("Team LinkLip")
                            .socialId("GOOGLE_123123123")
                            .socialType(Social.GOOGLE)
                            .build();
            userRepository.save(testUser);

            accessToken =
                    generateAccessToken(
                            socialId,
                            "software-maestro-13.linklip-application-2022.secret-key",
                            7200000);
            note1 = testUtils.saveNote("테스트 메모 1", null, testUser);
        }

        @Test
        @DisplayName("Note Content 상세보기")
        public void finNoteContent() throws Exception {

            // when
            Long contentId = note1.getId();
            ResultActions actions =
                    mockMvc.perform(
                            get("/content/v1/{contentId}", contentId)
                                    .header("Authorization", "Bearer " + accessToken));

            // then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content.id").value(contentId))
                    .andExpect(jsonPath("$.data.content.text").value(((Note) note1).getText()));
        }

        @Test
        @DisplayName("존재하지 않는 id의 Note Content 상세보기")
        public void findNotExistNoteContent() throws Exception {

            // when
            Long contentId = 987654321L;
            ResultActions actions =
                    mockMvc.perform(
                            get("/content/v1/{contentId}", contentId)
                                    .header("Authorization", "Bearer " + accessToken));

            // then
            actions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("메모 수정 통합테스트")
    class UpdateNoteContent {

        @BeforeEach
        public void createUser() {
            String socialId = "GOOGLE_123123123";
            testUser =
                    User.builder()
                            .nickName("Team LinkLip")
                            .socialId("GOOGLE_123123123")
                            .socialType(Social.GOOGLE)
                            .build();
            userRepository.save(testUser);

            accessToken =
                    generateAccessToken(
                            socialId,
                            "software-maestro-13.linklip-application-2022.secret-key",
                            7200000);
        }

        @Test
        @DisplayName("메모 컨텐츠 text 수정")
        public void updateText() throws Exception {

            // given
            Category category = testUtils.saveCategory("ToDo List", testUser);

            String text = "마트에서 사과 구매";
            Content savedContent = testUtils.saveNote(text, category, testUser);
            System.out.println("savedContent = " + savedContent);
            System.out.println("savedContent owner = " + savedContent.getOwner());
            System.out.println("testUser = " + testUser);
            System.out.println(contentRepository.findById(savedContent.getId()));

            UpdateNoteRequest updateNoteRequest = new UpdateNoteRequest();
            updateNoteRequest.setText("마트에서 포도 구매");

            // when
            System.out.println("Access Token: " + accessToken);
            ResultActions actions =
                    mockMvc.perform(
                            MockMvcRequestBuilders.patch(
                                            "/content/v1/note/{contentId}", savedContent.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken)
                                    .content(testUtils.asJsonString(updateNoteRequest)));

            // then
            actions.andExpect(status().isOk());
            assertThat(((Note) savedContent).getText()).isEqualTo(updateNoteRequest.getText());
        }

        @Test
        @DisplayName("메모 컨텐츠 text, 카테고리 수정")
        public void updateTextAndCategory() throws Exception {

            // given
            Category fromCategory = testUtils.saveCategory("ToDo List", testUser);
            Category toCategory = testUtils.saveCategory("ToDo", testUser);

            String text = "마트에서 사과 구매";
            Content savedContent = testUtils.saveNote(text, fromCategory, testUser);

            UpdateNoteRequest updateNoteRequest = new UpdateNoteRequest();
            updateNoteRequest.setText("마트에서 포도 구매");
            updateNoteRequest.setCategoryId(toCategory.getId());

            // when
            ResultActions actions =
                    mockMvc.perform(
                            MockMvcRequestBuilders.patch(
                                            "/content/v1/note/{contentId}", savedContent.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken)
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
            Category fromCategory = testUtils.saveCategory("ToDo List", testUser);
            Category toCategory = testUtils.saveCategory("ToDo", testUser);

            String text = "마트에서 사과 구매";
            Content savedContent = testUtils.saveNote(text, fromCategory, testUser);

            UpdateNoteRequest updateNoteRequest = new UpdateNoteRequest();
            updateNoteRequest.setText("");
            updateNoteRequest.setCategoryId(toCategory.getId());

            // when
            ResultActions actions =
                    mockMvc.perform(
                            MockMvcRequestBuilders.patch(
                                            "/content/v1/note/{contentId}", savedContent.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken)
                                    .content(testUtils.asJsonString(updateNoteRequest)));

            // then
            actions.andExpect(status().isBadRequest());
        }
    }

    private static String generateAccessToken(String socialId, String key, long expiredTimesMs) {
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
