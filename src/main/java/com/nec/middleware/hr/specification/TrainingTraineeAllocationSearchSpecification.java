package com.nec.middleware.hr.specification;

import com.nec.middleware.hr.dto.request.TrainingTraineeAllocationFilterRequestDto;
import com.nec.middleware.hr.entity.TrainingTraineeAllocation;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TrainingTraineeAllocationSearchSpecification {

    private TrainingTraineeAllocationSearchSpecification() {
    }

    public static Specification<TrainingTraineeAllocation> buildSpecification(
            TrainingTraineeAllocationFilterRequestDto filterRequest) {

        return (entityRoot, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> filterPredicates = new ArrayList<>();

            if (filterRequest == null) {
                return criteriaBuilder.and(
                        filterPredicates.toArray(new Predicate[0])
                );
            }

            if (filterRequest.getAllocationCode() != null
                    && !filterRequest.getAllocationCode().isBlank()) {

                filterPredicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(entityRoot.get("allocationCode")),
                                "%" + filterRequest.getAllocationCode().toLowerCase() + "%"
                        )
                );
            }

            if (filterRequest.getTraineeId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("trainee").get("id"),
                                filterRequest.getTraineeId()
                        )
                );
            }

            if (filterRequest.getUniversityId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("trainee").get("universityId"),
                                filterRequest.getUniversityId()
                        )
                );
            }

            if (filterRequest.getRegionId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("trainee").get("regionId"),
                                filterRequest.getRegionId()
                        )
                );
            }

            if (filterRequest.getTrainingClassId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("trainingClass").get("id"),
                                filterRequest.getTrainingClassId()
                        )
                );
            }

            if (filterRequest.getTrainingTypeId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("trainingClass").get("trainingTypeId"),
                                filterRequest.getTrainingTypeId()
                        )
                );
            }

            if (filterRequest.getStatusId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("status").get("id"),
                                filterRequest.getStatusId()
                        )
                );
            }

            if (filterRequest.getIsActive() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("isActive"),
                                filterRequest.getIsActive()
                        )
                );
            }

            return criteriaBuilder.and(
                    filterPredicates.toArray(new Predicate[0])
            );
        };
    }
}
