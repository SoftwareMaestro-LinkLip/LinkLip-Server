package com.linklip.linklipserver.domain;

import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 자동으로 where절에 deleted = false 이라는 SQL구문을 추가
@Where(clause = "is_deleted = false")
@DiscriminatorColumn(name = "type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Content extends JpaBaseDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @Column(insertable = false, updatable = false) // 읽기 전용으로 선언
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private boolean isDeleted;

    public void updateCategory(Category category) {
        this.category = category;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
