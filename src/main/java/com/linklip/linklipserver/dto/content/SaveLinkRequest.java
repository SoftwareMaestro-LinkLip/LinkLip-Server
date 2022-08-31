package com.linklip.linklipserver.dto.content;

import com.linklip.linklipserver.domain.Link;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SaveLinkRequest {
    @ApiModelProperty(required = true)
    @NotEmpty
    private String url;

    private String linkImg;
    private String title;
    private String text;

    public Link toEntity() {
        return Link.builder().linkUrl(url).linkImg(linkImg).title(title).text(text).build();
    }
}
