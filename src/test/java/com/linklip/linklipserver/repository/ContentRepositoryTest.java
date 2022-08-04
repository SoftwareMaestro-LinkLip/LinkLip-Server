package com.linklip.linklipserver.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.linklip.linklipserver.domain.Content;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest // DB와 관련된 컴포넌트만 메모리에 로딩, 테스트 종료 후 롤백도 같이 수행
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 데이터베이스에 테스트
class ContentRepositoryTest {

    @Autowired private ContentRepository contentRepository;

    @Nested
    @DisplayName("링크 저장 테스트")
    class saveContent {

        @Test
        @DisplayName("링크 url만 저장")
        public void saveOnlyUrl() {
            Content content = Content.builder().linkUrl("https://www.swmaestro.org/").build();

            Content savedContent = contentRepository.save(content);
            assertThat(content).isEqualTo(savedContent);
        }

        @Test
        @DisplayName("링크 url과 썸네일 저장")
        public void saveUrlAndThumbnail() {
            Content content =
                    Content.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .linkImg(
                                    "https://swmaestro.org/static/sw/renewal/images/common/logo_200.png")
                            .build();

            Content savedContent = contentRepository.save(content);
            assertThat(content).isEqualTo(savedContent);
        }

        @Test
        @DisplayName("링크 url, 썸네일, Title, Text 저장")
        public void saveLinkContent() {
            Content content =
                    Content.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .linkImg(
                                    "https://swmaestro.org/static/sw/renewal/images/common/logo_200.png")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                            .build();

            Content savedContent = contentRepository.save(content);
            assertThat(content).isEqualTo(savedContent);
        }
    }

    @Nested
    @DisplayName("링크 검색 테스트")
    class findContentsByTerm {

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));

        // 각 test 시작 이전에 실행
        @BeforeEach
        public void createContent() {
            Content content =
                    Content.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .linkImg(
                                    "https://swmaestro.org/static/sw/renewal/images/common/logo_200.png")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                            .build();
            contentRepository.save(content);
        }

        @Test
        @DisplayName("일반적인 검색어")
        public void findContentByNormalTerm() throws Exception {

            // given
            String term = "소프트";

            // when
            Page<Content> page =
                    contentRepository.findByTitleContainsOrTextContains(term, term, pageRequest);

            // then
            assertThat(page.getContent().size()).isEqualTo(1);
        }

        @Test
        @DisplayName("검색어 중간에 빈칸을 포함")
        public void findContentByBlankSpaceInTerm() throws Exception {

            // given
            String term = "웨어 마에";

            // when
            Page<Content> page =
                    contentRepository.findByTitleContainsOrTextContains(term, term, pageRequest);

            // then
            assertThat(page.getContent().size()).isEqualTo(1);
        }

        @Test
        @DisplayName("검색어에 아무것도 입력하지 않음")
        public void findContentByNullInTerm() throws Exception {

            // given
            Content content2 =
                    Content.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .build();
            contentRepository.save(content2);

            String term = "";

            // when
            Page<Content> page =
                    contentRepository.findAll(pageRequest);

            // then
            assertThat(page.getContent().size()).isEqualTo(2);
        }

        @Test
        @DisplayName("검색어 따로 없을 때, url만 포함한 데이터 검색")
        public void findByNullInTerm() throws Exception {

            // given
            Content content2 =
                    Content.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .build();
            contentRepository.save(content2);

            // when
            Page<Content> page =
                    contentRepository.findAll(pageRequest);

            // then
            assertThat(page.getContent().size()).isEqualTo(2);
        }

        @Test
        @DisplayName("여러개 검색 결과")
        public void findMultiResult() throws Exception {

            // given
            Content content2 =
                    Content.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                            .build();
            contentRepository.save(content2);

            Content content3 =
                    Content.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 12기 연수생 여러분...")
                            .build();
            contentRepository.save(content3);

            String term = "13";

            // when
            Page<Content> page =
                    contentRepository.findByTitleContainsOrTextContains(term, term, pageRequest);

            // then
            assertThat(page.getContent().size()).isEqualTo(2);
        }

        @Test
        @DisplayName("일치하는 검색 결과 없음")
        public void findZeroResult() throws Exception {

            // given
            String term = "1기";

            // when
            Page<Content> page =
                    contentRepository.findByTitleContainsOrTextContains(term, term, pageRequest);

            // then
            assertThat(page.getContent().size()).isEqualTo(0);
        }

        @Test
        @DisplayName("각 페이지 결과 갯수 확인")
        public void getEachPageResult() throws Exception {

            // given
            Content content2 =
                    Content.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                            .build();
            contentRepository.save(content2);

            Content content3 =
                    Content.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                            .build();
            contentRepository.save(content3);

            Content content4 =
                    Content.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                            .build();
            contentRepository.save(content4);

            String term = "13";

            // when
            pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
            Page<Content> page1 =
                    contentRepository.findByTitleContainsOrTextContains(term, term, pageRequest);

            pageRequest = PageRequest.of(1, 3, Sort.by(Sort.Direction.DESC, "id"));
            Page<Content> page2 =
                    contentRepository.findByTitleContainsOrTextContains(term, term, pageRequest);

            // then
            assertThat(page1.getContent().size()).isEqualTo(3); // 첫번째 페이지 결과
            assertThat(page2.getContent().size()).isEqualTo(1); // 두번째 페이지 결과
        }
    }
}
