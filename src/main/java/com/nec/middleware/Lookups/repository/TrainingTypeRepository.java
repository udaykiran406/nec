package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.TrainingTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingTypes, Long> {
}
