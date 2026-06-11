package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.ScoringTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoringTypeRepository extends JpaRepository<ScoringTypes, Long> {
}
