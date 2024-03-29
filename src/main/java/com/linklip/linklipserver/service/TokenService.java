package com.linklip.linklipserver.service;

import static com.linklip.linklipserver.constant.ErrorResponse.NOT_EXSIT_USER_ID;

import com.linklip.linklipserver.domain.RefreshToken;
import com.linklip.linklipserver.domain.User;
import com.linklip.linklipserver.dto.auth.ReissueTokenRequest;
import com.linklip.linklipserver.exception.ExpiredTokenException;
import com.linklip.linklipserver.exception.InvalidIdException;
import com.linklip.linklipserver.exception.NotValidTokenException;
import com.linklip.linklipserver.repository.TokenRepository;
import com.linklip.linklipserver.repository.UserRepository;
import com.linklip.linklipserver.util.JwtTokenUtils;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Value("${jwt.secret-key}")
    private String key;

    @Value("${jwt.access-token-expired-time-ms}")
    private Long accessTokenExpiredTime;

    @Value("${jwt.refresh-token-expired-time-ms}")
    private Long refreshTokenExpiredTime;

    @Transactional
    public void saveRefreshToken(Long userId, String token) {

        User user = getUser(userId);

        RefreshToken refreshToken = RefreshToken.builder().token(token).user(user).build();
        tokenRepository.save(refreshToken);
    }

    @Transactional
    public Map<String, String> reissueRefreshToken(ReissueTokenRequest request) {

        validate(request);

        Long userId = JwtTokenUtils.getUserId(request.getAccessToken(), key);
        String newAccessToken = JwtTokenUtils.generateToken(userId, key, accessTokenExpiredTime);
        String newRefreshToken = JwtTokenUtils.generateToken(userId, key, refreshTokenExpiredTime);

        changeToken(request, userId, newRefreshToken);

        return responseTokens(newAccessToken, newRefreshToken);
    }

    private User getUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new InvalidIdException(NOT_EXSIT_USER_ID.getMessage()));
    }

    private void validate(ReissueTokenRequest request) {
        try {
            if (JwtTokenUtils.isExpired(request.getRefreshToken(), key)) {
                throw new ExpiredTokenException("만료된 RefreshToken 입니다");
            }
        } catch (RuntimeException e) {
            throw new NotValidTokenException("인증되지 않은 사용자입니다");
        }
    }

    private void changeToken(ReissueTokenRequest request, Long userId, String newRefreshToken) {
        List<RefreshToken> tokenList = tokenRepository.findTokenList(userId);
        for (RefreshToken refreshToken : tokenList) {
            if (refreshToken.getToken().equals(request.getRefreshToken())) {
                refreshToken.changeToken(newRefreshToken);
                return;
            }
        }
        throw new NotValidTokenException("인증되지 않은 사용자입니다");
    }

    private Map<String, String> responseTokens(String newAccessToken, String newRefreshToken) {
        Map<String, String> tokens = new LinkedHashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);
        return tokens;
    }
}
