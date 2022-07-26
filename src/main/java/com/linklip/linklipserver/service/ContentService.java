package com.linklip.linklipserver.service;

import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.repository.ContentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // term이 파라미터로 넘어오지 않는 경우 모든 값을 조회할 수 있도록 term에 ""를 넣어줌
        if (term == null) term = "";

        return contentRepository.findByTitleOrTextContains(term, term);
    }
}
