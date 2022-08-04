package com.linklip.linklipserver.service;

import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.dto.content.SaveLinkRequest;
import com.linklip.linklipserver.repository.ContentRepository;
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

    // 컨텐츠 저장
    @Transactional
    public void saveLinkContent(SaveLinkRequest request) {
        Content content = request.toEntity();
        contentRepository.save(content);
    }

    public Page<Content> findContentByTerm(String term, Pageable pageable) {

        if (isTermNullOrEmpty(term)) return contentRepository.findAll(pageable);

        return contentRepository.findByTitleContainsOrTextContains(term, term, pageable);
    }

    static boolean isTermNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
