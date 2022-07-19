package com.linklip.linklipserver.domain;


import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Content {

    @Id @GeneratedValue
    @Column(name = "content_id")
    private Long id;

    @Column(nullable = false)
    private String linkUrl;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


}
