package com.linklip.linklipserver.controller;

import static com.linklip.linklipserver.constant.SuccessResponse.DELETE_CONTENT_SUCCESS;
import static com.linklip.linklipserver.constant.SuccessResponse.FIND_CONTENT_SUCCESS;
import static com.linklip.linklipserver.constant.SuccessResponse.FIND_LINK_LIST_SUCCESS;
import static com.linklip.linklipserver.constant.SuccessResponse.SAVE_LINK_SUCCESS;
import static com.linklip.linklipserver.constant.SuccessResponse.UPDATE_LINK_SUCCESS;

import com.linklip.linklipserver.dto.ServerResponse;
import com.linklip.linklipserver.dto.ServerResponseWithData;
import com.linklip.linklipserver.dto.content.FindContentRequest;
import com.linklip.linklipserver.dto.content.FindLinkListResponse;
import com.linklip.linklipserver.dto.content.SaveLinkRequest;
import com.linklip.linklipserver.dto.content.UpdateLinkRequest;
import com.linklip.linklipserver.service.ContentService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.annotations.*;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressFBWarnings("EI_EXPOSE_REP2")
@Api(value = "ContentController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/content")
public class ContentController {

    private final ContentService contentService;

    @ApiOperation(value = "링크 저장 API v1")
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

    @ApiOperation(value = "링크 검색 API v1")
    @ApiResponses({@ApiResponse(code = 200, message = "검색결과 조회 완료")})
    @GetMapping("/v1/link")
    public ResponseEntity<?> findLinkListV1(
            @ModelAttribute FindContentRequest request,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        return new ResponseEntity<>(
                new ServerResponseWithData(
                        FIND_LINK_LIST_SUCCESS.getStatus(),
                        FIND_LINK_LIST_SUCCESS.getSuccess(),
                        FIND_LINK_LIST_SUCCESS.getMessage(),
                        new FindLinkListResponse(
                                contentService.findContentList(request, pageable))),
                HttpStatus.OK);
    }

    @ApiOperation(value = "컨텐츠 상세정보 API v1")
    @ApiResponses({@ApiResponse(code = 200, message = "상세정보 조회 완료")})
    @GetMapping("/v1/link/{contentId}")
    public ResponseEntity<?> findLinkV1(@PathVariable Long contentId) {

        return new ResponseEntity<>(
                new ServerResponseWithData(
                        FIND_CONTENT_SUCCESS.getStatus(),
                        FIND_CONTENT_SUCCESS.getSuccess(),
                        FIND_CONTENT_SUCCESS.getMessage(),
                        contentService.findContent(contentId)),
                HttpStatus.OK);
    }

    @ApiOperation("링크 내용 수정 API v1")
    @ApiResponses({@ApiResponse(code = 200, message = "링크 내용 수정 완료")})
    @PatchMapping("/v1/link/{contentId}")
    public ResponseEntity<?> updateLinkV1(
            @PathVariable Long contentId, @RequestBody @Valid UpdateLinkRequest request) {

        contentService.updateLinkContent(contentId, request);

        return new ResponseEntity<>(
                new ServerResponse(
                        UPDATE_LINK_SUCCESS.getStatus(),
                        UPDATE_LINK_SUCCESS.getSuccess(),
                        UPDATE_LINK_SUCCESS.getMessage()),
                HttpStatus.OK);
    }

    @ApiOperation("컨텐츠 삭제 API v1")
    @ApiResponses({@ApiResponse(code = 200, message = "컨텐츠 삭제 완료")})
    @DeleteMapping("/v1/{contentId}")
    public ResponseEntity<?> deleteContentV1(@PathVariable Long contentId) {

        contentService.deleteContent(contentId);

        return new ResponseEntity<>(
                new ServerResponse(
                        DELETE_CONTENT_SUCCESS.getStatus(),
                        DELETE_CONTENT_SUCCESS.getSuccess(),
                        DELETE_CONTENT_SUCCESS.getMessage()),
                HttpStatus.OK);
    }
}
