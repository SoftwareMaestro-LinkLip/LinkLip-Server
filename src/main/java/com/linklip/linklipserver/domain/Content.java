package com.linklip.linklipserver.domain;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@NoArgsConstructor
public class Content {

    @Id
    @GeneratedValue
    @Column(name = "content_id")
    private Long id;

    @Column(nullable = false)
    private String linkUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Content(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}
