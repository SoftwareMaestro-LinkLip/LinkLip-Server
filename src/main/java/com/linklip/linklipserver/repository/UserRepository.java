package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsBySocialId(String SocialId);

    Optional<User> findBySocialId(String SocialId);
}
