package com.linklip.linklipserver.controller;

import static com.linklip.linklipserver.constant.SuccessResponse.*;

import com.linklip.linklipserver.domain.User;
import com.linklip.linklipserver.dto.ServerResponse;
import com.linklip.linklipserver.dto.ServerResponseWithData;
import com.linklip.linklipserver.dto.category.CategoryDto;
import com.linklip.linklipserver.dto.category.CreateCategoryRequest;
import com.linklip.linklipserver.dto.category.FindCategoryResponse;
import com.linklip.linklipserver.dto.category.UpdateCategoryRequest;
import com.linklip.linklipserver.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Api(value = "CategoryController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @ApiOperation(value = "카테고리 생성 API v1")
    @ApiResponses({@ApiResponse(code = 201, message = "카테고리 생성 완료")})
    @PostMapping("/v1")
    public ResponseEntity<?> createCategoryV1(
            @RequestBody @Valid CreateCategoryRequest request, @AuthenticationPrincipal User user) {

        categoryService.createCategory(request, user);

        return new ResponseEntity<>(
                new ServerResponse(
                        CREATE_CATEGORY_SUCCESS.getStatus(),
                        CREATE_CATEGORY_SUCCESS.getSuccess(),
                        CREATE_CATEGORY_SUCCESS.getMessage()),
                HttpStatus.CREATED);
    }

    @ApiOperation(value = "카테고리 조회 API v1")
    @ApiResponses({@ApiResponse(code = 200, message = "카테고리 조회 완료")})
    @GetMapping("/v1")
    @ResponseBody
    public ResponseEntity<?> getCategoryV1(@AuthenticationPrincipal User user) {

        List<CategoryDto> allCategory = categoryService.findAllCategoryByOwner(user);

        return new ResponseEntity<>(
                new ServerResponseWithData(
                        GET_CATEGORY_SUCCESS.getStatus(),
                        GET_CATEGORY_SUCCESS.getSuccess(),
                        GET_CATEGORY_SUCCESS.getMessage(),
                        new FindCategoryResponse(allCategory)),
                HttpStatus.OK);
    }

    @ApiOperation(value = "카테고리 수정 API v1")
    @ApiResponses({@ApiResponse(code = 200, message = "카테고리 수정 완료")})
    @PatchMapping("/v1/{categoryId}")
    @ResponseBody
    public ResponseEntity<?> updateCategoryV1(
            @PathVariable Long categoryId,
            @RequestBody @Valid UpdateCategoryRequest request,
            @AuthenticationPrincipal User user) {

        categoryService.updateCategory(categoryId, request, user);

        return new ResponseEntity<>(
                new ServerResponse(
                        UPDATE_CATEGORY_SUCCESS.getStatus(),
                        UPDATE_CATEGORY_SUCCESS.getSuccess(),
                        UPDATE_CATEGORY_SUCCESS.getMessage()),
                HttpStatus.OK);
    }

    @ApiOperation(value = "카테고리 삭제 API v1")
    @ApiResponses({@ApiResponse(code = 200, message = "카테고리 삭제 완료")})
    @DeleteMapping("/v1/{categoryId}")
    @ResponseBody
    public ResponseEntity<?> deleteCategoryV1(
            @PathVariable Long categoryId, @AuthenticationPrincipal User user) {

        categoryService.releaseCategory(categoryId, user);

        return new ResponseEntity<>(
                new ServerResponse(
                        DELETE_CATEGORY_SUCCESS.getStatus(),
                        DELETE_CATEGORY_SUCCESS.getSuccess(),
                        DELETE_CATEGORY_SUCCESS.getMessage()),
                HttpStatus.OK);
    }
}
