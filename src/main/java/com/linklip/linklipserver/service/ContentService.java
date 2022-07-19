package com.linklip.linklipserver.service;

import com.linklip.linklipserver.controller.dto.LinkSaveRequestDto;
import com.linklip.linklipserver.controller.dto.SaveLinkRequestDto;
import com.linklip.linklipserver.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    @Transactional
    public Long saveLink(SaveLinkRequestDto saveLinkRequestDto) {

    }

}
