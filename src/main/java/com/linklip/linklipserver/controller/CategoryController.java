package com.linklip.linklipserver.controller;

import static com.linklip.linklipserver.constant.SuccessResponse.CREATE_CATEGORY_SUCCESS;

import com.linklip.linklipserver.dto.ServerResponse;
import com.linklip.linklipserver.dto.category.CreateCategoryRequest;
import com.linklip.linklipserver.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "CategoryController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @ApiOperation(value = "카테고리 생성 API v1", notes = "[GYJB-105] 카테고리 생성")
    @ApiResponses({@ApiResponse(code = 201, message = "카테고리 생성 완료")})
    @PostMapping("/v1")
    public ServerResponse createCategoryV1(@RequestBody @Valid CreateCategoryRequest request) {

        categoryService.createCategory(request);

        return new ServerResponse(
                CREATE_CATEGORY_SUCCESS.getStatus(),
                CREATE_CATEGORY_SUCCESS.getSuccess(),
                CREATE_CATEGORY_SUCCESS.getMessage());
    }
}
