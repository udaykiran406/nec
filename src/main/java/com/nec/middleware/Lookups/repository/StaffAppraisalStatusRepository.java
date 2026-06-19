package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.StaffAppraisalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffAppraisalStatusRepository extends JpaRepository<StaffAppraisalStatus, Long> {
}