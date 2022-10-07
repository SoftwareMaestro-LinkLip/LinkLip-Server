package com.linklip.linklipserver.controller;

import static com.linklip.linklipserver.constant.SuccessResponse.TEST_PING_SUCCESS;

import com.linklip.linklipserver.dto.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "PingController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ping")
public class PingController {

    @ApiOperation(value = "상태 검사 API v1")
    @ApiResponses({@ApiResponse(code = 200, message = "상태 검사 완료")})
    @GetMapping
    public ResponseEntity<?> testPingV1() {

        return new ResponseEntity<>(
                new ServerResponse(
                        TEST_PING_SUCCESS.getStatus(),
                        TEST_PING_SUCCESS.getSuccess(),
                        TEST_PING_SUCCESS.getMessage()),
                HttpStatus.OK);
    }
}
