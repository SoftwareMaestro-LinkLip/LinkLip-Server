package com.linklip.linklipserver.IntegrationtTest;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.linklip.linklipserver.domain.Content;
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

    @Nested
    @DisplayName("링크 검색 통합테스트")
    class findContentsByTerm {

        @BeforeEach
        public void createContents() {
            String url1 = "https://www.swmaestro.org";
            String url2 = "https://www.naver.com";
            String title1 = "소프트웨어 마에스트로";
            saveContent(url1, title1, "소프트웨어 마에스트로 12기 연수생 여러분...");
            saveContent(url1, null, "소프트웨어 마에스트로 13기 연수생 여러분...");
            saveContent(url1, null, null);
            saveContent(url2, null, null);
        }

        @DisplayName("일반적인 검색어")
        @Test
        public void findContentByNormalTerm() throws Exception {

            // when
            String term = "13기";
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
        @DisplayName("검색어 중간에 빈칸을 포함")
        public void findContentByBlankSpaceInTerm() throws Exception {

            // when
            String term = "웨어 마에";
            ResultActions actions =
                    mockMvc.perform(
                            get("/content/v1/link")
                                    .param("term", term)
                                    .param("page", "0")
                                    .param("size", "20"));

            // then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.pageDto.content").value(hasSize(2)));
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
                            jsonPath("$.data.pageDto.content").value(hasSize(4))); // 모든 Content 출력
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
                            jsonPath("$.data.pageDto.content").value(hasSize(0))); // 모든 Content 출력
        }

        @Test
        @DisplayName("검색어 없이 조회")
        public void findContentByNotTerm() throws Exception {

            // when
            ResultActions actions =
                    mockMvc.perform(get("/content/v1/link").param("page", "0").param("size", "20"));

            // then
            actions.andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.data.pageDto.content").value(hasSize(4))); // 모든 Content 출력
        }

        @Test
        @DisplayName("마지막 페이지 결과 갯수 확인")
        public void getLastPageResult() throws Exception {

            // given
            String baseUrl = "https://www.swmaestro.org";
            String baseTitle = "소프트웨어 마에스트로";
            saveContent(baseUrl, baseTitle, "소프트웨어 마에스트로 12기 연수생 여러분...");

            // when
            ResultActions actions =
                    mockMvc.perform(get("/content/v1/link").param("page", "1").param("size", "3"));

            // then
            actions.andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.data.pageDto.content").value(hasSize(2))); // 모든 Content 출력
        }
    }

    public Content saveContent(String url, String title, String text) {
        Content content = Content.builder().linkUrl(url).title(title).text(text).build();
        return contentRepository.save(content);
    }
}
