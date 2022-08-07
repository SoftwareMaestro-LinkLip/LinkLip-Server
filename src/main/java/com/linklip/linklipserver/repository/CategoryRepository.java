package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
