package com.linklip.linklipserver.domain;

import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content extends JpaBaseDomain {

    @Id
    @GeneratedValue
    @Column(name = "content_id")
    private Long id;

    @Column(nullable = false)
    private String linkUrl;

    private String linkImg;

    private String title;

    @Lob // column type을 longtext로 설정
    private String text;

    @Builder
    public Content(String linkUrl, String linkImg, String title, String text) {
        this.linkUrl = linkUrl;
        this.linkImg = linkImg;
        this.title = title;
        this.text = text;
    }
}
