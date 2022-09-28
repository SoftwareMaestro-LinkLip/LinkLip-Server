package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.RefreshToken;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user.id=:userId")
    List<RefreshToken> findTokenList(@Param("userId") Long userId);
}
