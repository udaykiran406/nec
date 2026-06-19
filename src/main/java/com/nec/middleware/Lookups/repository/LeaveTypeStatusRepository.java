package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.LeaveTypeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveTypeStatusRepository extends JpaRepository<LeaveTypeStatus, Long> {
}