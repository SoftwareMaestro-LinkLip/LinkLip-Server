package com.linklip.linklipserver.service;

import com.linklip.linklipserver.domain.Content;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ContentServiceTest {

    @Autowired ContentService contentService;

    @Test
    @DisplayName("url, imageUrl 저장 테스트")
    public void saveLinkUrlAndImage() throws Exception {
        // given
        Content content =
                Content.builder()
                        .linkUrl("https://www.swmaestro.org/")
                        .linkImg(
                                "https://swmaestro.org/static/sw/renewal/images/common/logo_200.png")
                        .build();

        // when
        contentService.saveContent(content);

        // then

    }
}
