package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.TrainingClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingClassRepository extends JpaRepository<TrainingClass, Long> {

    List<TrainingClass> findByIsDeletedFalse();

    boolean existsByClassNameIgnoreCaseAndIsDeletedFalse(String className);
}