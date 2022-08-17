package com.linklip.linklipserver.dto.content;

import com.linklip.linklipserver.domain.Content;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ContentDto {

    private Long id;
    private String url;
    private String linkImg;
    private String title;
    private String categoryName;

    public ContentDto(Content content) {
        this.id = content.getId();
        this.url = content.getLinkUrl();
        this.linkImg = content.getLinkImg();
        this.title = content.getTitle();
        if (content.getCategory() != null) {
            this.categoryName = content.getCategory().getName();
        }
    }

    public ContentDto(ContentDto content) {
        this.id = content.getId();
        this.url = content.getUrl();
        this.linkImg = content.getLinkImg();
        this.title = content.getTitle();
        if (content.getCategoryName() != null) {
            this.categoryName = content.getCategoryName();
        }
    }
}
