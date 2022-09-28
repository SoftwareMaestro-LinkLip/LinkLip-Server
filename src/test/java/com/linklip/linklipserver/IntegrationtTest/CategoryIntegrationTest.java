package com.linklip.linklipserver.IntegrationtTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.linklip.linklipserver.TestUtils;
import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.domain.Social;
import com.linklip.linklipserver.domain.User;
import com.linklip.linklipserver.dto.category.CreateCategoryRequest;
import com.linklip.linklipserver.dto.category.UpdateCategoryRequest;
import com.linklip.linklipserver.repository.CategoryRepository;
import com.linklip.linklipserver.repository.ContentRepository;
import com.linklip.linklipserver.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CategoryIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ContentRepository contentRepository;

    @Autowired private TestUtils testUtils;

    @Value("${jwt.secret-key}")
    private String key;

    @Value("${jwt.token-expired-time-ms}")
    private Long expiredTime;

    User testUser;
    String accessToken;

    @Nested
    @DisplayName("카테고리 생성 통합테스트")
    class CreateCategoryIntegrationTest {

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

            accessToken = generateAccessToken(socialId, key, expiredTime);
        }

        @Test
        @DisplayName("일반적인 카테고리 명인 경우 201")
        public void createNormalCategory() throws Exception {
            String name = "취업 공고";
            CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest();
            createCategoryRequest.setName(name);

            mockMvc.perform(
                            post("/category/v1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken)
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
                                    .header("Authorization", "Bearer " + accessToken)
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
                                    .header("Authorization", "Bearer " + accessToken)
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
                                    .header("Authorization", "Bearer " + accessToken)
                                    .content(testUtils.asJsonString(createCategoryRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("카테고리 조회 통합테스트")
    class FindCategoryIntegrationTest {

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

            accessToken = generateAccessToken(socialId, key, expiredTime);
        }

        @Test
        @DisplayName("카테고리 조회에 성공한 경우 200")
        public void findCategory() throws Exception {
            mockMvc.perform(
                            get("/category/v1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("카테고리 수정 통합테스트")
    class UpdateCategoryIntegrationTest {

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

            accessToken = generateAccessToken(socialId, key, expiredTime);
        }

        @Test
        @DisplayName("일반적인 카테고리명 수정")
        public void updateCategory() throws Exception {

            // given
            Category category = testUtils.saveCategory("운동", testUser);
            long categoryId = category.getId();

            // when
            UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
            updateCategoryRequest.setName("스포츠");

            ResultActions actions =
                    mockMvc.perform(
                            patch("/category/v1/{categoryId}", categoryId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken)
                                    .content(testUtils.asJsonString(updateCategoryRequest)));

            // then
            actions.andExpect(status().isOk()).andDo(print());
        }

        @Test
        @DisplayName("이미 존재하는 카테고리명으로 수정")
        public void updateCategoryByPresentName() throws Exception {

            // given
            Category category1 = testUtils.saveCategory("운동", testUser);
            testUtils.saveCategory("스포츠", testUser);
            long categoryId = category1.getId();

            // when
            UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
            updateCategoryRequest.setName("스포츠");

            ResultActions actions =
                    mockMvc.perform(
                            patch("/category/v1/{categoryId}", categoryId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken)
                                    .content(testUtils.asJsonString(updateCategoryRequest)));

            // then
            actions.andExpect(status().isOk());
        }

        @Test
        @DisplayName("빈값으로 카테고리명 수정")
        public void updateCategoryByNullName() throws Exception {

            // given
            Category category = testUtils.saveCategory("운동", testUser);
            long categoryId = category.getId();

            // when
            UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
            updateCategoryRequest.setName("");

            ResultActions actions =
                    mockMvc.perform(
                            patch("/category/v1/{categoryId}", categoryId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken)
                                    .content(testUtils.asJsonString(updateCategoryRequest)));

            // then
            actions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("카테고리 삭제 통합테스트")
    class DeleteCategoryIntegrationTest {

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

            accessToken = generateAccessToken(socialId, key, expiredTime);
        }

        @Test
        @DisplayName("컨텐츠가 존재하지 않는 카테고리 수정")
        public void deleteEmptyCategory() throws Exception {
            // given
            Category category1 = testUtils.saveCategory("운동", testUser);
            testUtils.saveCategory("개발", testUser);

            // when
            ResultActions actions =
                    mockMvc.perform(
                            delete("/category/v1/{categoryId}", category1.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken));

            // then
            actions.andExpect(status().isOk());

            List<Category> all = categoryRepository.findAll();
            assertThat(all.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("컨텐츠가 존재하는 카테고리 수정")
        public void deleteCategoryHavingContents() throws Exception {
            // given
            Category category1 = testUtils.saveCategory("홈페이지", testUser);
            Category category2 = testUtils.saveCategory("개발", testUser);

            Content content1 =
                    testUtils.saveLink("naver.com", "Naver", "뉴스 로그인 회원가입", category1, testUser);
            Content content2 =
                    testUtils.saveLink("soma.org", "소프트웨어 마에스트로", "소마 홈페이지", category1, testUser);
            Content content3 =
                    testUtils.saveLink("soma.org", "소프트웨어 마에스트로", "소마 홈페이지", category2, testUser);

            // when
            ResultActions actions =
                    mockMvc.perform(
                            delete("/category/v1/{categoryId}", category1.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + accessToken));

            // then
            actions.andExpect(status().isOk());

            List<Category> all = categoryRepository.findAll();
            assertThat(all.size()).isEqualTo(1);

            Optional<Content> findContent1 = contentRepository.findById(content1.getId());
            Optional<Content> findContent2 = contentRepository.findById(content2.getId());
            Optional<Content> findContent3 = contentRepository.findById(content3.getId());

            assertThat(findContent1.get().getCategory()).isEqualTo(null);
            assertThat(findContent2.get().getCategory()).isEqualTo(null);
            assertThat(findContent3.get().getCategory().getId()).isEqualTo(category2.getId());
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
