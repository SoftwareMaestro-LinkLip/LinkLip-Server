package com.linklip.linklipserver.service;

import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.dto.category.CategoryDto;
import com.linklip.linklipserver.dto.category.CreateCategoryRequest;
import com.linklip.linklipserver.dto.category.UpdateCategoryRequest;
import com.linklip.linklipserver.exception.InvalidIdException;
import com.linklip.linklipserver.repository.CategoryRepository;
import com.linklip.linklipserver.repository.ContentRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.linklip.linklipserver.constant.ErrorResponse.NOT_EXSIT_CATEGORY_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
    private final ContentRepository contentRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void createCategory(CreateCategoryRequest request) {
        Category category = request.toEntity();
        categoryRepository.save(category);
    }

    public List<CategoryDto> findAllCategory() {
        List<Category> all = categoryRepository.findAllByOrderByName();
        return all.stream()
                .map(c -> CategoryDto.builder().id(c.getId()).name(c.getName()).build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateCategory(Long categoryId, UpdateCategoryRequest request) {
        Category category =
                categoryRepository
                        .findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException(NOT_EXSIT_CATEGORY_ID.getMessage()));
        category.update(request.getName());
    }

    @Transactional
    public void releaseCategory(Long categoryId) {
        contentRepository.releaseCategoryByCategoryId(categoryId);
        try {
            categoryRepository.deleteById(categoryId);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidIdException(NOT_EXSIT_CATEGORY_ID.getMessage());
        }
    }
}
