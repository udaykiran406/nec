 package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.LeavePlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeavePlanStatusRepository extends JpaRepository<LeavePlanStatus, Long> {
}
