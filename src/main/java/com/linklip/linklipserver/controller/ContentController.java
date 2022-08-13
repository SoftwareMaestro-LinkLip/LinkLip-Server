package com.linklip.linklipserver.controller;

import static com.linklip.linklipserver.constant.SuccessResponse.FIND_LINK_SUCCESS;
import static com.linklip.linklipserver.constant.SuccessResponse.SAVE_LINK_SUCCESS;

import com.linklip.linklipserver.dto.ServerResponse;
import com.linklip.linklipserver.dto.ServerResponseWithData;
import com.linklip.linklipserver.dto.content.FindContentRequest;
import com.linklip.linklipserver.dto.content.FindLinkResponse;
import com.linklip.linklipserver.dto.content.SaveLinkRequest;
import com.linklip.linklipserver.service.ContentService;
import io.swagger.annotations.*;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> saveLinkV1(@RequestBody @Valid SaveLinkRequest request) {

        contentService.saveLinkContent(request);

        return new ResponseEntity<>(
                new ServerResponse(
                        SAVE_LINK_SUCCESS.getStatus(),
                        SAVE_LINK_SUCCESS.getSuccess(),
                        SAVE_LINK_SUCCESS.getMessage()),
                HttpStatus.CREATED);
    }

    @ApiOperation(value = "링크 검색 API v1", notes = "[GYJB-75] term을 통한 링크 검색")
    @ApiResponses({@ApiResponse(code = 200, message = "검색결과 조회 완료")})
    @GetMapping("/v1/link")
    public ResponseEntity<?> findLinkListV1(
            @ModelAttribute FindContentRequest request,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        return new ResponseEntity<>(
                new ServerResponseWithData(
                        FIND_LINK_SUCCESS.getStatus(),
                        FIND_LINK_SUCCESS.getSuccess(),
                        FIND_LINK_SUCCESS.getMessage(),
                        new FindLinkResponse(contentService.findContentList(request, pageable))),
                HttpStatus.OK);
    }

}
