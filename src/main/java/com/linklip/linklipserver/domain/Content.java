package com.linklip.linklipserver.domain;

import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 자동으로 where절에 deleted = false 이라는 SQL구문을 추가
@Where(clause = "deleted = false")
public class Content extends JpaBaseDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @Column(nullable = false)
    private String linkUrl;

    private String linkImg;

    private String title;

    @Lob // column type을 longtext로 설정
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private boolean deleted;

    @Builder
    public Content(String linkUrl, String linkImg, String title, String text, Category category) {
        this.linkUrl = linkUrl;
        this.linkImg = linkImg;
        this.title = title;
        this.text = text;
        this.category = category;
        this.deleted = false;
    }

    public void update(String title, Category category) {
        this.title = title;
        this.category = category;
    }

    public void softDelete() {
        this.deleted = true;
    }
}
