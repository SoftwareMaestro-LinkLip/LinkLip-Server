package com.linklip.linklipserver.domain.content;

import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.domain.User;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("link")
public class Link extends Content {

    private String linkUrl;

    private String linkImg;

    private String title;

    @Lob // column type을 longtext로 설정
    private String text;

    @Builder
    public Link(
            String linkUrl,
            String linkImg,
            String title,
            String text,
            Category category,
            User owner) {
        this.linkUrl = linkUrl;
        this.linkImg = linkImg;
        this.title = title;
        this.text = text;
        super.updateCategory(category);
        super.updateOwner(owner);
    }

    public void update(String title, Category category) {
        this.title = title;
        super.updateCategory(category);
    }
}
