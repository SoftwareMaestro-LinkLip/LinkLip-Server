package com.linklip.linklipserver.service;

import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.dto.content.ContentDto;
import com.linklip.linklipserver.dto.content.FindContentRequest;
import com.linklip.linklipserver.dto.content.SaveLinkRequest;
import com.linklip.linklipserver.repository.CategoryRepository;
import com.linklip.linklipserver.repository.ContentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final CategoryRepository categoryRepository;

    // 컨텐츠 저장
    @Transactional
    public void saveLinkContent(SaveLinkRequest request) {
        Content content = request.toEntity();
        contentRepository.save(content);
    }

    // 동적 쿼리 해결을 위한 QueryDSL 방안도 고려 필요
    public Page<ContentDto> findContent(FindContentRequest request, Pageable pageable) {

        String categoryTerm = request.getCategoryTerm();
        String allTerm = request.getAllTerm();

        if (isCategoryTermPresent(categoryTerm)) {
            List<Category> categoryList = categoryRepository.findAllByNameContains(categoryTerm);
            return contentRepository
                    .findByCategoryIn(categoryList, pageable)
                    .map(c -> new ContentDto(c));
        }

        if (isAllTermNullOrEmpty(allTerm)) {
            return contentRepository.findAll(pageable).map(c -> new ContentDto(c));
        }

        return contentRepository
                .findByTitleContainsOrTextContains(allTerm, allTerm, pageable)
                .map(c -> new ContentDto(c));
    }

    static boolean isCategoryTermPresent(String str) {
        return str != null;
    }

    static boolean isAllTermNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
