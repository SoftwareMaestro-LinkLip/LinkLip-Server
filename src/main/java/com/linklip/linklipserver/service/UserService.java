package com.linklip.linklipserver.service;

import com.linklip.linklipserver.domain.User;
import com.linklip.linklipserver.exception.InvalidIdException;
import com.linklip.linklipserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUser(String userId) {
        return userRepository
                .findBySocialId(userId)
                .orElseThrow(() -> new InvalidIdException("존재하지 않는 userId입니다"));
    }
}
