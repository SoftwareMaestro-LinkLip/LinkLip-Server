package com.linklip.linklipserver.controller;

import static com.linklip.linklipserver.constant.SuccessResponse.*;

import com.linklip.linklipserver.dto.ServerResponse;
import com.linklip.linklipserver.dto.ServerResponseWithData;
import com.linklip.linklipserver.dto.content.*;
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

    @ApiOperation(value = "컨텐츠 검색 API v1")
    @ApiResponses({@ApiResponse(code = 200, message = "검색결과 조회 완료")})
    @GetMapping("/v1/link")
    public ResponseEntity<?> findContentListV1(
            @ModelAttribute FindContentRequest request,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 20)
                    Pageable pageable) {

        return new ResponseEntity<>(
                new ServerResponseWithData(
                        FIND_CONTENT_LIST_SUCCESS.getStatus(),
                        FIND_CONTENT_LIST_SUCCESS.getSuccess(),
                        FIND_CONTENT_LIST_SUCCESS.getMessage(),
                        contentService.findContentList(request, pageable)),
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
                        new FindContentResponse(contentService.findContent(contentId))),
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

    @ApiOperation(value = "메모 저장 API v1")
    @ApiResponses({@ApiResponse(code = 201, message = "메모 저장 완료")})
    @PostMapping("/v1/note")
    public ResponseEntity<?> saveNoteV1(@RequestBody @Valid SaveNoteRequest request) {

        contentService.saveNoteContent(request);

        return new ResponseEntity<>(
                new ServerResponse(
                        SAVE_NOTE_SUCCESS.getStatus(),
                        SAVE_NOTE_SUCCESS.getSuccess(),
                        SAVE_NOTE_SUCCESS.getMessage()),
                HttpStatus.CREATED);
    }
}
