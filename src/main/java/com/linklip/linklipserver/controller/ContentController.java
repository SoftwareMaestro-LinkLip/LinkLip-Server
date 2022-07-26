package com.linklip.linklipserver.controller;

import static com.linklip.linklipserver.constant.SuccessResponse.FIND_LINK_SUCCESS;
import static com.linklip.linklipserver.constant.SuccessResponse.SAVE_LINK_SUCCESS;

import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.dto.ServerResponse;
import com.linklip.linklipserver.dto.ServerResponseWithData;
import com.linklip.linklipserver.dto.content.ContentDto;
import com.linklip.linklipserver.dto.content.FindLinkResponse;
import com.linklip.linklipserver.dto.content.SaveLinkRequest;
import com.linklip.linklipserver.service.ContentService;
import io.swagger.annotations.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value = "ContentController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/content")
public class ContentController {

    private final ContentService contentService;

    @ApiOperation(value = "링크 저장 API v1", notes = "[GYJB-79] 링크 url, image, title, text DB에 저장")
    @ApiResponses({@ApiResponse(code = 201, message = "링크 저장 완료")})
    @PostMapping("/v1/link")
    public ServerResponse saveLinkV1(@RequestBody @Valid SaveLinkRequest request) {
        Content content = request.toEntity();
        contentService.saveContent(content);

        return new ServerResponse(
                SAVE_LINK_SUCCESS.getStatus(),
                SAVE_LINK_SUCCESS.getSuccess(),
                SAVE_LINK_SUCCESS.getMessage());
    }

    @ApiOperation(value = "링크 검색 API v1", notes = "[GYJB-75] term을 통한 링크 검색")
    @ApiResponses({@ApiResponse(code = 200, message = "검색결과 조회 완료")})
    @GetMapping("/v1/link")
    public ServerResponseWithData<?> saveLinkV1(@RequestParam(required = false) String term) {

        List<Content> contents = contentService.findContentByTerm(term);
        List<ContentDto> contentDtos =
                contents.stream()
                        .map(content -> new ContentDto(content))
                        .collect(Collectors.toList());

        return new ServerResponseWithData(
                FIND_LINK_SUCCESS.getStatus(),
                FIND_LINK_SUCCESS.getSuccess(),
                FIND_LINK_SUCCESS.getMessage(),
                new FindLinkResponse(contentDtos));
    }
}
