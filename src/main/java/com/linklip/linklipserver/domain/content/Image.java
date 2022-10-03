package com.linklip.linklipserver.domain.content;

import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.domain.User;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("image")
public class Image extends Content {

    private String imageUrl;

    @Builder
    public Image(String imageUrl, Category category, User owner) {
        this.imageUrl = imageUrl;
        super.updateCategory(category);
        super.updateOwner(owner);
    }
}
