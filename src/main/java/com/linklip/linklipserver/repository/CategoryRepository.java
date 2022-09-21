package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.Category;
import com.linklip.linklipserver.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderByName();

    Optional<Category> findByIdAndOwner(Long id, User owner);
}
