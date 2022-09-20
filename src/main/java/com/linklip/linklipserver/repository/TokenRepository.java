package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<RefreshToken, Long> {}
