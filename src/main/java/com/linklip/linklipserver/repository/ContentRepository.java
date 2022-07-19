package com.linklip.linklipserver.repository;

import com.linklip.linklipserver.domain.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class ContentRepository {

    private final EntityManager em;

    public void save(Content content) {
        em.persist(content);
    }

}
