package com.nec.middleware.hr.specification;

import com.nec.middleware.hr.dto.request.TrainingAttendanceFilterRequestDto;
import com.nec.middleware.hr.entity.TrainingAttendanceRecord;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class TrainingAttendanceSearchSpecification {

    private TrainingAttendanceSearchSpecification() {
    }

    public static Specification<TrainingAttendanceRecord> build(
            TrainingAttendanceFilterRequestDto request) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request == null) {
                return criteriaBuilder.conjunction();
            }

            if (request.getTrainingClassId() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("trainingClass").get("id"),
                                request.getTrainingClassId()
                        )
                );
            }

            if (request.getTraineeId() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("trainee").get("id"),
                                request.getTraineeId()
                        )
                );
            }

            if (request.getFromDate() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("attendanceDate"),
                                request.getFromDate()
                        )
                );
            }

            if (request.getToDate() != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("attendanceDate"),
                                request.getToDate()
                        )
                );
            }

            if (request.getIsPresent() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("isPresent"),
                                request.getIsPresent()
                        )
                );
            }

            if (request.getIsActive() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("isActive"),
                                request.getIsActive()
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}