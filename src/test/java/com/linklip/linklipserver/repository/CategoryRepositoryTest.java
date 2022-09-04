package com.linklip.linklipserver.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.linklip.linklipserver.domain.Category;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 데이터베이스에 테스트
class CategoryRepositoryTest {

    @Autowired private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("카테고리 생성 테스트")
    class SaveCategory {

        @Test
        @DisplayName("일반적인 카테고리 명 생성")
        public void createCategory() {
            Category category = Category.builder().name("취업 공고").build();
            Category createdCategory = categoryRepository.save(category);
            assertThat(createdCategory).isEqualTo(category);
        }

        @Test
        @DisplayName("중복되는 카테고리 명 생성")
        public void createDuplicatedCategory() {
            Category category1 = Category.builder().name("취업 공고").build();
            Category createdCategory1 = categoryRepository.save(category1);
            Category category2 = Category.builder().name("취업 공고").build();
            Category createdCategory2 = categoryRepository.save(category2);

            assertThat(createdCategory1).isEqualTo(category1);
            assertThat(createdCategory2).isEqualTo(category2);
            assertThat(createdCategory2).isNotEqualTo(category1);
        }

        @Test
        @DisplayName("null이라는 카테고리 명 생성")
        public void createNullCategory() {
            Category category = Category.builder().name("null").build();
            Category createdCategory = categoryRepository.save(category);

            assertThat(createdCategory).isEqualTo(category);
        }

        @Test
        @DisplayName("Empty 카테고리 명 생성")
        public void createEmptyCategory() {
            Category category = Category.builder().name("").build();
            Category createdCategory = categoryRepository.save(category);

            assertThat(createdCategory).isEqualTo(category);
        }
    }

    @Nested
    @DisplayName("카테고리 조회 테스트")
    class FindCategory {
        @Test
        @DisplayName("이름 순으로 카테고리 조회")
        public void findCategoryNormal() {
            Category category1 = Category.builder().name("채용 정보").build();
            Category category2 = Category.builder().name("개발 정보").build();
            categoryRepository.save(category1);
            categoryRepository.save(category2);

            List<Category> categoryList = categoryRepository.findAllByOrderByName();
            assertThat(categoryList.size()).isEqualTo(2);
            assertThat(categoryList.get(0)).isEqualTo(category2);
            assertThat(categoryList.get(1)).isEqualTo(category1);
        }
    }

    @Nested
    @DisplayName("카테고리 삭제 테스트")
    class DeleteCategory {
        @Test
        @DisplayName("일반적인 카테고리 삭제")
        public void deleteCategoryNormal() {
            // given
            Category category1 = Category.builder().name("채용 정보").build();
            Category category2 = Category.builder().name("개발 정보").build();
            categoryRepository.save(category1);
            categoryRepository.save(category2);

            // when
            categoryRepository.deleteById(category1.getId());

            // then
            List<Category> categoryList = categoryRepository.findAllByOrderByName();
            assertThat(categoryList.size()).isEqualTo(1);
        }
    }
}
