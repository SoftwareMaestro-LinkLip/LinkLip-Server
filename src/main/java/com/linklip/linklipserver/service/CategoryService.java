package com.linklip.linklipserver.service;

import static com.linklip.linklipserver.constant.ErrorResponse.NOT_EXSIT_CATEGORY_ID;

import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.domain.User;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
    private final ContentRepository contentRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void createCategory(CreateCategoryRequest request, User owner) {

        Category category = Category.builder().name(request.getName()).owner(owner).build();
        categoryRepository.save(category);
    }

    public List<CategoryDto> findAllCategoryByOwner(User owner) {
        List<Category> all = categoryRepository.findByOwnerOrderByName(owner);
        return all.stream()
                .map(c -> CategoryDto.builder().id(c.getId()).name(c.getName()).build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateCategory(Long categoryId, UpdateCategoryRequest request, User owner) {
        Category category =
                categoryRepository
                        .findByIdAndOwner(categoryId, owner)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                NOT_EXSIT_CATEGORY_ID.getMessage()));
        category.update(request.getName());
    }

    @Transactional
    public void releaseCategory(Long categoryId, User owner) {
        contentRepository.releaseCategoryById(categoryId, owner.getId());
        try {
            categoryRepository.deleteById(categoryId);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidIdException(NOT_EXSIT_CATEGORY_ID.getMessage());
        }
    }
}
