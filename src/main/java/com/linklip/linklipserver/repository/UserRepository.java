package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialId(String socialId);
}
