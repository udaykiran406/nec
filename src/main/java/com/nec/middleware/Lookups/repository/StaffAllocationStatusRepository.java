package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.StaffAllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffAllocationStatusRepository extends JpaRepository<StaffAllocationStatus, Long> {
}