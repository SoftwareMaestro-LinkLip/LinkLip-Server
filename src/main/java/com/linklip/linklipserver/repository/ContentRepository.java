package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.domain.Content;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {

    Page<Content> findAll(Pageable pageable);

    Page<Content> findByTitleContainsOrTextContains(String title, String text, Pageable pageable);

    Page<Content> findByCategoryIn(List<Category> categories, Pageable pageable);
}
