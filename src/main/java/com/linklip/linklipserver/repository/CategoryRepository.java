package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderByName();

    List<Category> findAllByNameContains(String name);
}
