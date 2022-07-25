package com.linklip.linklipserver.domain;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content {

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

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Content(String linkUrl, String linkImg, String title, String text) {
        this.linkUrl = linkUrl;
        this.linkImg = linkImg;
        this.title = title;
        this.text = text;
    }
}
