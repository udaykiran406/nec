package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.LeaveRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRequestStatusRepository extends JpaRepository<LeaveRequestStatus, Long> {
}