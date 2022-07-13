package com.linklip.linklipserver.controller;

import com.linklip.linklipserver.controller.dto.TestRequestDto;
import com.linklip.linklipserver.controller.dto.TestResponseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

@Api(value = "SwaggerTestController")   // 클래스를 Swagger 리소스 대상으로 표시
@RestController
@RequestMapping("/api")
public class SwaggerTestController {

    @ApiOperation(value = "test", notes = "테스트입니다.")    // 요청 URL에 매핑된 API 에 대한 설명
    @ApiResponses({     // 응답에 대한 설명
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "server error"),
            @ApiResponse(code = 404, message = "not found")
    })
    @PostMapping("/test")
    public TestResponseDto test(@RequestBody TestRequestDto testRequestDto) {
        return TestResponseDto.builder()
                .name(testRequestDto.getName())
                .age(testRequestDto.getAge())
                .build();
    }
}