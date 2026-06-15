package com.nec.middleware.hr.specification;

import com.nec.middleware.hr.dto.request.TrainingScheduleFilterRequestDto;
import com.nec.middleware.hr.entity.TrainingSchedule;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class TrainingScheduleSearchSpecification {

    private TrainingScheduleSearchSpecification() {
    }

    public static Specification<TrainingSchedule> build(
            TrainingScheduleFilterRequestDto request) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request == null) {
                return criteriaBuilder.conjunction();
            }

            if (request.getScheduleCode() != null &&
                    !request.getScheduleCode().isBlank()) {

                predicates.add(
                        criteriaBuilder.like(
                                root.get("scheduleCode"),
                                "%" + request.getScheduleCode() + "%"
                        )
                );
            }

            if (request.getTrainingClassId() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("trainingClass").get("id"),
                                request.getTrainingClassId())
                );
            }

            if (request.getVenue() != null &&
                    !request.getVenue().isBlank()) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("venue")),
                                "%" + request.getVenue().toLowerCase() + "%"
                        )
                );
            }

            if (request.getLocation() != null &&
                    !request.getLocation().isBlank()) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("location")),
                                "%" + request.getLocation().toLowerCase() + "%"
                        )
                );
            }

            if (request.getFromDate() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("fromDate"),
                                request.getFromDate())
                );
            }

            if (request.getToDate() != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("toDate"),
                                request.getToDate())
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0]));
        };
    }
}