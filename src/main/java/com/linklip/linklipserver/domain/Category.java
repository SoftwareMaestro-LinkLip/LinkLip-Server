package com.linklip.linklipserver.domain;

import javax.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends JpaBaseDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Builder
    public Category(String name) {
        this.name = name;
    }

    public void update(String name) {
        this.name = name;
    }
}
