package com.nec.middleware.hr.specification;

import com.nec.middleware.hr.dto.request.TrainingClassListRequestDto;
import com.nec.middleware.hr.entity.TrainingClass;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class TrainingClassSearchSpecification {

    private TrainingClassSearchSpecification() {
    }

    public static Specification<TrainingClass> build(
            TrainingClassListRequestDto request) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request == null) {
                return criteriaBuilder.conjunction();
            }

            if (request.getClassCode() != null &&
                    !request.getClassCode().isBlank()) {

                predicates.add(
                        criteriaBuilder.like(
                                root.get("classCode"),
                                "%" + request.getClassCode() + "%"
                        )
                );
            }

            if (request.getClassName() != null &&
                    !request.getClassName().isBlank()) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("className")),
                                "%" + request.getClassName().toLowerCase() + "%"
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

            if (request.getTrainingTypeId() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("trainingType").get("id"),
                                request.getTrainingTypeId())
                );
            }

            if (request.getStatusId() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("status").get("id"),
                                request.getStatusId())
                );
            }

            if (request.getRegionId() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("region").get("id"),
                                request.getRegionId())
                );
            }

            if (request.getDistrictId() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("district").get("id"),
                                request.getDistrictId())
                );
            }

            if (request.getCityId() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("city").get("id"),
                                request.getCityId())
                );
            }

            if (request.getUniversityId() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("university").get("id"),
                                request.getUniversityId())
                );
            }

            if (request.getTrainerTotId() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("trainerTot").get("id"),
                                request.getTrainerTotId())
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0]));
        };
    }
}