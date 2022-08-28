package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentRepository extends JpaRepository<Content, Long> {

    @EntityGraph(attributePaths = {"category"})
    Page<Content> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    @Query(
            "SELECT c FROM Content c WHERE c.category.id=:categoryId AND (c.title LIKE %:term% OR c.text LIKE %:term%)")
    Page<Content> findByCategoryAndTerm(
            @Param("categoryId") Long categoryId, @Param("term") String term, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT c FROM Content c WHERE c.category.id=:categoryId")
    Page<Content> findByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT c FROM Content c WHERE c.title LIKE %:term% OR c.text LIKE %:term%")
    Page<Content> findByTerm(@Param("term") String term, Pageable pageable);

    @Modifying(clearAutomatically = true) // executeUpdate 같은 Annotation
    @Query("UPDATE Content c SET c.category = null WHERE c.category.id = :categoryId")
    void releaseCategoryByCategoryId(@Param("categoryId") Long categoryId);
}
