package org.example.repositories;

import org.example.models.SimilarTrademark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimilarTrademarkRepository extends JpaRepository<SimilarTrademark, Integer> {
    List<SimilarTrademark> getBySearchId(Integer searchId);
}

