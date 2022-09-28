package com.linklip.linklipserver.service;

import static com.linklip.linklipserver.constant.ErrorResponse.NOT_EXSIT_USER_ID;

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

    public User findUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new InvalidIdException(NOT_EXSIT_USER_ID.getMessage()));
    }
}
