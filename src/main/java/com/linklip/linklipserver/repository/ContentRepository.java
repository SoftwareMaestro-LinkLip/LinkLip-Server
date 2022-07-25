package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.Content;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentRepository {

    private final EntityManager em;

    public void save(Content content) {
        em.persist(content);
    }
}
