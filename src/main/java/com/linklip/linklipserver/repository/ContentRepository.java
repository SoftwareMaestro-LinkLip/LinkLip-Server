package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findByTitleOrTextContains(String title, String text);
}
