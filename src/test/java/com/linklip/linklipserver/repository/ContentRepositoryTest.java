package com.linklip.linklipserver.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.linklip.linklipserver.TestUtils;
import com.linklip.linklipserver.domain.Category;
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
    @Autowired private CategoryRepository categoryRepository;

    @Autowired private TestUtils testUtils;

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
    class findContents {

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        Category category;

        // 각 test 시작 이전에 실행
        @BeforeEach
        public void createContent() {

            category = Category.builder().name("활동").build();
            categoryRepository.save(category);

            Content content1 =
                    Content.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .linkImg(
                                    "https://swmaestro.org/static/sw/renewal/images/common/logo_200.png")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                            .category(category)
                            .build();
            Content content2 =
                    Content.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .linkImg(
                                    "https://swmaestro.org/static/sw/renewal/images/common/logo_200.png")
                            .title("멋쟁이 사자처럼")
                            .text("멋쟁이 사자처럼 7기 연수생 여러분...")
                            .category(category)
                            .build();

            contentRepository.save(content1);
            contentRepository.save(content2);
        }

        @Nested
        @DisplayName("카테고리 해당 컨텐츠")
        class findContentsByCategory {

            @Test
            @DisplayName("컨텐츠 불러오기")
            public void findContent() throws Exception {

                // when
                Long categoryId = category.getId();
                Page<Content> page = contentRepository.findByCategory(categoryId, pageRequest);

                // then
                assertThat(page.getContent().size()).isEqualTo(2);
            }

            @Test
            @DisplayName("일반적인 검색어")
            public void findContentByNormalTerm() throws Exception {

                // when
                Long categoryId = category.getId();
                String term = "소프트";
                Page<Content> page =
                        contentRepository.findByCategoryAndTerm(categoryId, term, pageRequest);

                // then
                assertThat(page.getContent().size()).isEqualTo(1);
            }

            @Test
            @DisplayName("검색어 중간에 빈칸을 포함")
            public void findContentByBlankSpaceInTerm() throws Exception {

                // when
                Long categoryId = category.getId();
                String term = "웨어 마에";
                Page<Content> page =
                        contentRepository.findByCategoryAndTerm(categoryId, term, pageRequest);

                // then
                assertThat(page.getContent().size()).isEqualTo(1);
            }

            @Test
            @DisplayName("여러개 검색 결과")
            public void findMultiResult() throws Exception {

                // given
                Content content1 =
                        Content.builder()
                                .linkUrl("https://www.swmaestro.org/")
                                .title("소프트웨어 마에스트로")
                                .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                                .category(category)
                                .build();
                Content content2 =
                        Content.builder()
                                .linkUrl("https://www.swmaestro.org/")
                                .title("소프트웨어 마에스트로")
                                .text("소프트웨어 마에스트로 12기 연수생 여러분...")
                                .build();

                contentRepository.save(content1);
                contentRepository.save(content2);

                // when
                Long categoryId = category.getId();
                String term = "13";
                Page<Content> page =
                        contentRepository.findByCategoryAndTerm(categoryId, term, pageRequest);

                // then
                assertThat(page.getContent().size()).isEqualTo(2);
            }

            @Test
            @DisplayName("일치하는 검색 결과 없음")
            public void findZeroResult() throws Exception {

                // when
                Long categoryId = category.getId();
                String term = "1기";
                Page<Content> page =
                        contentRepository.findByCategoryAndTerm(categoryId, term, pageRequest);

                // then
                assertThat(page.getContent().size()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("전체 검색어")
        class findContentsByTerm {

            @Test
            @DisplayName("일반적인 검색어")
            public void findContentByNormalTerm() throws Exception {

                // given
                String term = "소프트";

                // when
                Page<Content> page = contentRepository.findByTerm(term, pageRequest);

                // then
                assertThat(page.getContent().size()).isEqualTo(1);
            }

            @Test
            @DisplayName("검색어 중간에 빈칸을 포함")
            public void findContentByBlankSpaceInTerm() throws Exception {

                // given
                String term = "웨어 마에";

                // when
                Page<Content> page = contentRepository.findByTerm(term, pageRequest);

                // then
                assertThat(page.getContent().size()).isEqualTo(1);
            }

            @Test
            @DisplayName("검색어 null 이거나 empty")
            public void findContentByNullInTerm() throws Exception {

                // given
                Content content2 = Content.builder().linkUrl("https://www.swmaestro.org/").build();
                contentRepository.save(content2);

                // when
                Page<Content> page = contentRepository.findAll(pageRequest);

                // then
                assertThat(page.getContent().size()).isEqualTo(3);
            }

            @Test
            @DisplayName("검색어 일치하는 검색 결과 없음")
            public void findZeroResult() throws Exception {

                // given
                String term = "1기";

                // when
                Page<Content> page = contentRepository.findByTerm(term, pageRequest);

                // then
                assertThat(page.getContent().size()).isEqualTo(0);
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
                Page<Content> page = contentRepository.findByTerm(term, pageRequest);

                // then
                assertThat(page.getContent().size()).isEqualTo(2);
            }
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
            Page<Content> page1 = contentRepository.findByTerm(term, pageRequest);

            pageRequest = PageRequest.of(1, 3, Sort.by(Sort.Direction.DESC, "id"));
            Page<Content> page2 = contentRepository.findByTerm(term, pageRequest);

            // then
            assertThat(page1.getContent().size()).isEqualTo(3); // 첫번째 페이지 결과
            assertThat(page2.getContent().size()).isEqualTo(1); // 두번째 페이지 결과
        }
    }

    @Nested
    @DisplayName("컨텐츠 상세보기")
    class findContent {

        @Test
        @DisplayName("일반적인 경우")
        public void getContent() throws Exception {

            // given
            Content content =
                    Content.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 12기 연수생 여러분...")
                            .build();
            contentRepository.save(content);

            // when
            Content result = contentRepository.findById(content.getId()).get();

            // then
            assertThat(result.getId()).isEqualTo(content.getId());
        }
    }
}
