package com.linklip.linklipserver.controller;

import static com.linklip.linklipserver.constant.SuccessResponse.REISSUE_TOKEN_SUCCESS;

import com.linklip.linklipserver.dto.ServerResponseWithData;
import com.linklip.linklipserver.dto.auth.ReissueTokenRequest;
import com.linklip.linklipserver.service.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "TokenController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class TokenController {

    private final TokenService tokenService;

    @ApiOperation(value = "토큰 재발급 API v1")
    @ApiResponses({@ApiResponse(code = 201, message = "토큰 재발급 완료")})
    @PostMapping("/v1/refresh-token")
    public ResponseEntity<ServerResponseWithData<Map<String, String>>> reissueToken(
            @RequestBody @Valid ReissueTokenRequest request) {

        return new ResponseEntity<>(
                new ServerResponseWithData<>(
                        REISSUE_TOKEN_SUCCESS.getStatus(),
                        REISSUE_TOKEN_SUCCESS.getSuccess(),
                        REISSUE_TOKEN_SUCCESS.getMessage(),
                        tokenService.reissueRefreshToken(request)),
                HttpStatus.OK);
    }
}
