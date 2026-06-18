package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.TrainingAttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TrainingAttendanceRecordRepository
        extends JpaRepository<TrainingAttendanceRecord, Long>,
        JpaSpecificationExecutor<TrainingAttendanceRecord> {

    Optional<TrainingAttendanceRecord>
    findByTrainingClassIdAndTraineeIdAndAttendanceDate(
            Long trainingClassId,
            Long traineeId,
            LocalDate attendanceDate
    );

}