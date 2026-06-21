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

    public static Specification<TrainingTraineeAllocation>
    buildSpecification(
            TrainingTraineeAllocationFilterRequestDto filterRequest) {

        return (entityRoot, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> filterPredicates = new ArrayList<>();

            if (filterRequest == null) {
                return criteriaBuilder.and(
                        filterPredicates.toArray(new Predicate[0])
                );
            }

            // Allocation Code
            if (filterRequest.getAllocationCode() != null
                    && !filterRequest.getAllocationCode().isBlank()) {

                filterPredicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        entityRoot.get("allocationCode")
                                ),
                                "%" + filterRequest
                                        .getAllocationCode()
                                        .toLowerCase() + "%"
                        )
                );
            }

            // Trainee
            if (filterRequest.getTraineeId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("trainee").get("id"),
                                filterRequest.getTraineeId()
                        )
                );
            }

            // Region
            if (filterRequest.getRegionId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("region").get("id"),
                                filterRequest.getRegionId()
                        )
                );
            }

            // District
            if (filterRequest.getDistrictId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("district").get("id"),
                                filterRequest.getDistrictId()
                        )
                );
            }

            // City
            if (filterRequest.getCityId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("city").get("id"),
                                filterRequest.getCityId()
                        )
                );
            }

            // University
            if (filterRequest.getUniversityId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("university").get("id"),
                                filterRequest.getUniversityId()
                        )
                );
            }

            // Training Class
            if (filterRequest.getTrainingClassId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("trainingClass").get("id"),
                                filterRequest.getTrainingClassId()
                        )
                );
            }

            // Training Type
            if (filterRequest.getTrainingTypeId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("trainingType").get("id"),
                                filterRequest.getTrainingTypeId()
                        )
                );
            }

            // Status
            if (filterRequest.getStatusId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("status").get("id"),
                                filterRequest.getStatusId()
                        )
                );
            }

            // Active Status
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