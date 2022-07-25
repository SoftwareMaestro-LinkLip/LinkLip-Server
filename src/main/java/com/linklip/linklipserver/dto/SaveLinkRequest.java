package com.linklip.linklipserver.dto;

import com.linklip.linklipserver.domain.Content;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SaveLinkRequest {
    @NotEmpty private String url;
    private String linkImg;
    private String title;
    private String text;

    public Content toEntity() {
        return Content.builder().linkUrl(url).linkImg(linkImg).title(title).text(text).build();
    }
}
