package com.linklip.linklipserver.service;

import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    // 컨텐츠 저장
    @Transactional
    public void saveContent(Content content) {
        contentRepository.save(content);
    }

    public List<Content> findContentByTerm(String term) {
        return contentRepository.findByTitleOrTextContains(term, term);
    }
}
