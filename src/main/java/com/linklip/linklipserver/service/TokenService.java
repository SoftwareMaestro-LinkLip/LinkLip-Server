package com.linklip.linklipserver.service;

import com.linklip.linklipserver.domain.RefreshToken;
import com.linklip.linklipserver.domain.User;
import com.linklip.linklipserver.repository.TokenRepository;
import com.linklip.linklipserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Transactional
    public void saveRefreshToken(String socialId, String token) {

        User user = userRepository.findBySocialId(socialId).get();

        RefreshToken refreshToken = RefreshToken.builder().token(token).user(user).build();
        tokenRepository.save(refreshToken);
    }
}
