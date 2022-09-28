package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsBySocialId(String socialId);
}
