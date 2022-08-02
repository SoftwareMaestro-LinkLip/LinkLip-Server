package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {

    Page<Content> findByTitleOrTextContains(String title, String text, Pageable pageable);
}
