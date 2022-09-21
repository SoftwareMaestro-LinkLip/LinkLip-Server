package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.Content;
import com.linklip.linklipserver.domain.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentRepository extends JpaRepository<Content, Long> {

    @EntityGraph(attributePaths = {"category"})
    Page<Content> findAllByOwner(Pageable pageable, User owner);

    Optional<Content> findByIdAndOwner(Long id, User owner);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT c FROM Content c WHERE c.owner.id=:ownerId AND c.category.id=:categoryId")
    Page<Content> findByCategoryAndOwner(
            @Param("categoryId") Long categoryId,
            Pageable pageable,
            @Param("ownerId") Long ownerId);

    @EntityGraph(attributePaths = {"category"})
    @Query(
            "SELECT c FROM Content c WHERE c.owner.id=:ownerId AND (treat(c as Link).linkUrl LIKE %:term% OR treat(c as Link).title LIKE %:term% OR treat(c as Link).text LIKE %:term% OR treat(c as Note).text LIKE %:term%)")
    Page<Content> findByTermAndOwner(
            @Param("term") String term, Pageable pageable, @Param("ownerId") Long ownerId);

    @EntityGraph(attributePaths = {"category"})
    @Query(
            "SELECT c FROM Content c WHERE c.owner.id=:ownerId AND c.category.id=:categoryId AND (treat(c as Link).linkUrl LIKE %:term% OR treat(c as Link).title LIKE %:term% OR treat(c as Link).text LIKE %:term% OR treat(c as Note).text LIKE %:term%)")
    Page<Content> findByCategoryAndTermAndOwner(
            @Param("categoryId") Long categoryId,
            @Param("term") String term,
            Pageable pageable,
            @Param("ownerId") Long ownerId);

    @Modifying(clearAutomatically = true) // executeUpdate 같은 Annotation
    @Query(
            "UPDATE Content c SET c.category = null WHERE c.owner.id=:ownerId AND c.category.id = :categoryId")
    void releaseCategoryById(@Param("categoryId") Long categoryId, @Param("ownerId") Long ownerId);
}
