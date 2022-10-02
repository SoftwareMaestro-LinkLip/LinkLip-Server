package com.linklip.linklipserver.dto.content;

import com.linklip.linklipserver.domain.content.Note;
import com.linklip.linklipserver.dto.category.CategoryDto;
import lombok.Getter;

@Getter
public class NoteDto extends ContentDto {
    private final String text;

    public NoteDto(Note content) {
        this.id = content.getId();
        this.type = content.getType();
        this.text = content.getText();
        if (content.getCategory() != null) {
            this.category = new CategoryDto(content.getCategory());
        }
    }
}
