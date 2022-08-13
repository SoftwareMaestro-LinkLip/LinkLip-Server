package com.linklip.linklipserver.service;

import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.dto.content.ContentDto;
import com.linklip.linklipserver.dto.content.FindContentRequest;
import com.linklip.linklipserver.dto.content.SaveLinkRequest;
import com.linklip.linklipserver.repository.CategoryRepository;
import com.linklip.linklipserver.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContentService {

    private final CategoryRepository categoryRepository;
    private final ContentRepository contentRepository;

    // 컨텐츠 저장
    @Transactional
    public void saveLinkContent(SaveLinkRequest request) {
        Content content = request.toEntity();
        contentRepository.save(content);
    }

    public Page<ContentDto> findContentList(FindContentRequest request, Pageable pageable) {

        String term = request.getTerm();
        Long categoryId = request.getCategoryId();

        Page<Content> page = null;

        if (categoryId != null && StringUtils.hasText(term)) {
            page = contentRepository.findByCategoryAndTerm(categoryId, term, pageable);
        }

        if (categoryId != null && !StringUtils.hasText(term)) {
            page = contentRepository.findByCategory(categoryId, pageable);
        }

        if (categoryId == null && StringUtils.hasText(term)) {
            page = contentRepository.findByTerm(term, pageable);
        }

        if (categoryId == null && !StringUtils.hasText(term)) {
            page = contentRepository.findAll(pageable);
        }

        return page.map(c -> new ContentDto(c));
    }
}
