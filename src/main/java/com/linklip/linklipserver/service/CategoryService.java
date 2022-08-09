package com.linklip.linklipserver.service;

import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.dto.category.CategoryDto;
import com.linklip.linklipserver.dto.category.CreateCategoryRequest;
import com.linklip.linklipserver.dto.category.UpdateCategoryRequest;
import com.linklip.linklipserver.repository.CategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
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
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 categoryId입니다."));
        category.update(request.getName());
    }
}
