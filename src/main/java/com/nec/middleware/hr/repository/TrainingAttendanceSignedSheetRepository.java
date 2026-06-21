package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.TrainingAttendanceSignedSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingAttendanceSignedSheetRepository
        extends JpaRepository<TrainingAttendanceSignedSheet, Long> {
}