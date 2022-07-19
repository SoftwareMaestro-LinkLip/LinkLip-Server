package com.linklip.linklipserver.domain;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Content {

    @Id @GeneratedValue
    @Column(name = "content_id")
    private Long id;

    @Column(nullable = false)
    private String linkUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
