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
@DiscriminatorValue("note")
public class Note extends Content {
    @Lob // column type을 longtext로 설정
    private String text;

    @Builder
    public Note(String text, Category category, User owner) {
        this.text = text;
        super.updateCategory(category);
        super.updateOwner(owner);
    }

    public void update(String text, Category category) {
        this.text = text;
        super.updateCategory(category);
    }
}
