package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.Content;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findByTitleOrTextContains(String title, String text);
}
