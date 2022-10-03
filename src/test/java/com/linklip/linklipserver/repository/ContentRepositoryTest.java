package com.linklip.linklipserver.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.linklip.linklipserver.domain.*;
import com.linklip.linklipserver.domain.content.Content;
import com.linklip.linklipserver.domain.content.Image;
import com.linklip.linklipserver.domain.content.Link;
import com.linklip.linklipserver.domain.content.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest // DB와 관련된 컴포넌트만 메모리에 로딩, 테스트 종료 후 롤백도 같이 수행
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 데이터베이스에 테스트
class ContentRepositoryTest {

    @Autowired private UserRepository userRepository;
    @Autowired private ContentRepository contentRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Nested
    @DisplayName("링크 저장 테스트")
    class SaveLink {

        @Test
        @DisplayName("링크 url만 저장")
        public void saveOnlyUrl() {
            Content content = Link.builder().linkUrl("https://www.swmaestro.org/").build();

            Content savedContent = contentRepository.save(content);
            assertThat(content).isEqualTo(savedContent);
        }

        @Test
        @DisplayName("링크 url과 썸네일 저장")
        public void saveUrlAndThumbnail() {
            Content content =
                    Link.builder()
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
                    Link.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .linkImg(
                                    "https://swmaestro.org/static/sw/renewal/images/common/logo_200.png")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                            .build();

            Content savedContent = contentRepository.save(content);
            assertThat(content).isEqualTo(savedContent);
        }

        @Test
        @DisplayName("링크 url, 썸네일, Title, Text, 카테고리 저장")
        public void saveLinkContentWithCategory() {
            Category category = Category.builder().name("취업 정보").build();
            Content content =
                    Link.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .linkImg(
                                    "https://swmaestro.org/static/sw/renewal/images/common/logo_200.png")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                            .category(category)
                            .build();

            Content savedContent = contentRepository.save(content);
            assertThat(content).isEqualTo(savedContent);
        }
    }

    @Nested
    @DisplayName("링크 검색 테스트")
    class FindContents {

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        Category category;
        User user1;
        User user2;

        // 각 test 시작 이전에 실행
        @BeforeEach
        public void createContent() {

            user1 =
                    User.builder()
                            .nickName("Team LinkLip")
                            .socialId("GOOGLE_123123123")
                            .socialType(Social.GOOGLE)
                            .build();
            user2 =
                    User.builder()
                            .nickName("Team neighbor")
                            .socialId("GOOGLE_456456456")
                            .socialType(Social.GOOGLE)
                            .build();
            userRepository.save(user1);
            userRepository.save(user2);

            category = Category.builder().name("활동").build();
            categoryRepository.save(category);

            Content content1 =
                    Link.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .linkImg(
                                    "https://swmaestro.org/static/sw/renewal/images/common/logo_200.png")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                            .category(category)
                            .owner(user1)
                            .build();
            Content content2 =
                    Link.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .linkImg(
                                    "https://swmaestro.org/static/sw/renewal/images/common/logo_200.png")
                            .title("멋쟁이 사자처럼")
                            .text("멋쟁이 사자처럼 7기 연수생 여러분...")
                            .category(category)
                            .owner(user2)
                            .build();
            Content content3 =
                    Link.builder()
                            .linkUrl("https://www.swmaestro.org/linklip")
                            .linkImg(
                                    "https://swmaestro.org/static/sw/renewal/images/common/logo_200.png")
                            .title("링클립 화이팅!!!")
                            .text("링클립 화이팅팅팅~~~")
                            .category(category)
                            .owner(user1)
                            .build();

            contentRepository.save(content1);
            contentRepository.save(content2);
            contentRepository.save(content3);
        }

        @Nested
        @DisplayName("카테고리 해당 컨텐츠")
        class FindContentsByCategory {

            @Test
            @DisplayName("컨텐츠 불러오기")
            public void findContent() throws Exception {

                // when
                Long categoryId = category.getId();
                Page<Content> page =
                        contentRepository.findByCategoryAndOwner(
                                categoryId, pageRequest, user1.getId());

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
                        contentRepository.findByCategoryAndTermAndOwner(
                                categoryId, term, pageRequest, user1.getId());

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
                        contentRepository.findByCategoryAndTermAndOwner(
                                categoryId, term, pageRequest, user1.getId());

                // then
                assertThat(page.getContent().size()).isEqualTo(1);
            }

            @Test
            @DisplayName("여러개 검색 결과")
            public void findMultiResult() throws Exception {

                // given
                Content content1 =
                        Link.builder()
                                .linkUrl("https://www.swmaestro.org/")
                                .title("소프트웨어 마에스트로")
                                .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                                .category(category)
                                .owner(user1)
                                .build();
                Content content2 =
                        Link.builder()
                                .linkUrl("https://www.swmaestro.org/")
                                .title("소프트웨어 마에스트로")
                                .text("소프트웨어 마에스트로 12기 연수생 여러분...")
                                .owner(user1)
                                .build();

                contentRepository.save(content1);
                contentRepository.save(content2);

                // when
                Long categoryId = category.getId();
                String term = "13";
                Page<Content> page =
                        contentRepository.findByCategoryAndTermAndOwner(
                                categoryId, term, pageRequest, user1.getId());

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
                        contentRepository.findByCategoryAndTermAndOwner(
                                categoryId, term, pageRequest, user1.getId());

                // then
                assertThat(page.getContent().size()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("전체 검색어")
        class FindContentsByTerm {

            @Test
            @DisplayName("일반적인 검색어")
            public void findContentByNormalTerm() throws Exception {

                // given
                String term = "소프트";

                // when
                Page<Content> page =
                        contentRepository.findByTermAndOwner(term, pageRequest, user1.getId());

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
                        contentRepository.findByTermAndOwner(term, pageRequest, user1.getId());

                // then
                assertThat(page.getContent().size()).isEqualTo(1);
            }

            @Test
            @DisplayName("검색어 null 이거나 empty")
            public void findContentByNullInTerm() throws Exception {

                // given
                Content content =
                        Link.builder().linkUrl("https://www.swmaestro.org/").owner(user1).build();
                contentRepository.save(content);

                // when
                Page<Content> page = contentRepository.findAllByOwner(pageRequest, user1);

                // then
                assertThat(page.getContent().size()).isEqualTo(3);
            }

            @Test
            @DisplayName("검색어 일치하는 검색 결과 없음")
            public void findZeroResult() throws Exception {

                // given
                String term = "1기";

                // when
                Page<Content> page =
                        contentRepository.findByTermAndOwner(term, pageRequest, user1.getId());

                // then
                assertThat(page.getContent().size()).isEqualTo(0);
            }

            @Test
            @DisplayName("여러개 검색 결과")
            public void findMultiResult() throws Exception {

                // given
                Content content2 =
                        Link.builder()
                                .linkUrl("https://www.swmaestro.org/")
                                .title("소프트웨어 마에스트로")
                                .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                                .owner(user1)
                                .build();
                contentRepository.save(content2);

                Content content3 =
                        Link.builder()
                                .linkUrl("https://www.swmaestro.org/")
                                .title("소프트웨어 마에스트로")
                                .text("소프트웨어 마에스트로 12기 연수생 여러분...")
                                .owner(user1)
                                .build();
                contentRepository.save(content3);

                String term = "13";

                // when
                Page<Content> page =
                        contentRepository.findByTermAndOwner(term, pageRequest, user1.getId());

                // then
                assertThat(page.getContent().size()).isEqualTo(2);
            }
        }

        @Test
        @DisplayName("각 페이지 결과 갯수 확인")
        public void getEachPageResult() throws Exception {

            // given
            Content content2 =
                    Link.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                            .owner(user1)
                            .build();
            contentRepository.save(content2);

            Content content3 =
                    Link.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                            .owner(user1)
                            .build();
            contentRepository.save(content3);

            Content content4 =
                    Link.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 13기 연수생 여러분...")
                            .owner(user1)
                            .build();
            contentRepository.save(content4);

            String term = "13";

            // when
            pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
            Page<Content> page1 =
                    contentRepository.findByTermAndOwner(term, pageRequest, user1.getId());

            pageRequest = PageRequest.of(1, 3, Sort.by(Sort.Direction.DESC, "id"));
            Page<Content> page2 =
                    contentRepository.findByTermAndOwner(term, pageRequest, user1.getId());

            // then
            assertThat(page1.getContent().size()).isEqualTo(3); // 첫번째 페이지 결과
            assertThat(page2.getContent().size()).isEqualTo(1); // 두번째 페이지 결과
        }
    }

    @Nested
    @DisplayName("컨텐츠 상세보기")
    class FindContent {

        @Test
        @DisplayName("일반적인 경우")
        public void getContent() throws Exception {

            // given
            User user1 =
                    User.builder()
                            .nickName("Team LinkLip")
                            .socialId("GOOGLE_123123123")
                            .socialType(Social.GOOGLE)
                            .build();
            userRepository.save(user1);

            Content content =
                    Link.builder()
                            .linkUrl("https://www.swmaestro.org/")
                            .title("소프트웨어 마에스트로")
                            .text("소프트웨어 마에스트로 12기 연수생 여러분...")
                            .owner(user1)
                            .build();
            contentRepository.save(content);

            // when
            Content result = contentRepository.findByIdAndOwner(content.getId(), user1).get();

            // then
            assertThat(result.getId()).isEqualTo(content.getId());
        }
    }

    @Nested
    @DisplayName("메모 저장 테스트")
    class SaveNoteContent {

        User user1 =
                User.builder()
                        .nickName("Team LinkLip")
                        .socialId("GOOGLE_123123123")
                        .socialType(Social.GOOGLE)
                        .build();

        @Test
        @DisplayName("카테고리 설정 없이 메모 저장")
        public void saveOnlyText() {
            Content content = Note.builder().text("TOPCIT 지원 기간 9월 중순까지!!").owner(user1).build();

            Content savedContent = contentRepository.save(content);
            assertThat(savedContent).isEqualTo(content);
        }

        @Test
        @DisplayName("카테고리 설정하여 메모 저장")
        public void saveTextWithCategory() {
            Category category = Category.builder().name("시험 접수").build();
            Category savedCategory = categoryRepository.save(category);

            Content content =
                    Note.builder()
                            .text("TOPCIT 지원 기간 9월 중순까지!!")
                            .category(savedCategory)
                            .owner(user1)
                            .build();

            Content savedContent = contentRepository.save(content);
            assertThat(savedContent).isEqualTo(content);
            assertThat(savedContent.getCategory()).isEqualTo(savedCategory);
        }
    }

    @Nested
    @DisplayName("사진 저장 테스트")
    class SaveImageContent {

        User user1 =
                User.builder()
                        .nickName("Team LinkLip")
                        .socialId("GOOGLE_123123123")
                        .socialType(Social.GOOGLE)
                        .build();

        @Test
        @DisplayName("카테고리 설정 없이 사진 저장")
        public void saveOnlyImage() {
            Content content =
                    Image.builder()
                            .imageUrl(
                                    "https://"
                                            + bucket
                                            + ".s3.ap-northeast-2.amazonaws.com/1b4f69e3-56f3-4841-889a-7f1941b75e47-test.png")
                            .owner(user1)
                            .build();
            Content savedContent = contentRepository.save(content);

            assertThat(savedContent).isEqualTo(content);
        }

        @Test
        @DisplayName("카테고리 설정하여 사진 저장")
        public void saveImageWithCategory() {
            Category category = Category.builder().name("시험 접수").build();
            Category savedCategory = categoryRepository.save(category);
            Content content =
                    Image.builder()
                            .imageUrl(
                                    "https://"
                                            + bucket
                                            + ".s3.ap-northeast-2.amazonaws.com/1b4f69e3-56f3-4841-889a-7f1941b75e47-test.png")
                            .category(savedCategory)
                            .owner(user1)
                            .build();

            Content savedContent = contentRepository.save(content);

            assertThat(savedContent).isEqualTo(content);
            assertThat(savedContent.getCategory()).isEqualTo(savedCategory);
        }
    }
}
