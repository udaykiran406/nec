package com.nec.middleware.hr.specification;

import com.nec.middleware.hr.dto.request.AaqilFilterRequestDto;
import com.nec.middleware.hr.entity.Aaqil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AaqilSearchSpecification {

    private AaqilSearchSpecification() {
    }

    public static Specification<Aaqil> buildSpecification(
            AaqilFilterRequestDto filterRequest) {

        return (entityRoot, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> filterPredicates = new ArrayList<>();

            // Aaqil ID
            if (filterRequest.getAaqilId() != null
                    && !filterRequest.getAaqilId().isBlank()) {

                filterPredicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        entityRoot.get("aaqilId")),
                                "%" + filterRequest.getAaqilId()
                                        .toLowerCase() + "%"
                        )
                );
            }

            // Full Name
            if (filterRequest.getFullName() != null
                    && !filterRequest.getFullName().isBlank()) {

                filterPredicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        entityRoot.get("fullName")),
                                "%" + filterRequest.getFullName()
                                        .toLowerCase() + "%"
                        )
                );
            }

            // Phone
            if (filterRequest.getPhone() != null
                    && !filterRequest.getPhone().isBlank()) {

                filterPredicates.add(
                        criteriaBuilder.like(
                                entityRoot.get("phone"),
                                "%" + filterRequest.getPhone() + "%"
                        )
                );
            }

            // Email
            if (filterRequest.getEmail() != null
                    && !filterRequest.getEmail().isBlank()) {

                filterPredicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        entityRoot.get("email")),
                                "%" + filterRequest.getEmail()
                                        .toLowerCase() + "%"
                        )
                );
            }

            // Aaqil Type
            if (filterRequest.getAaqilTypeId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("aaqilType").get("id"),
                                filterRequest.getAaqilTypeId()
                        )
                );
            }

            // Gender
            if (filterRequest.getGenderId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("gender").get("id"),
                                filterRequest.getGenderId()
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

            // Status
            if (filterRequest.getStatusId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("status").get("id"),
                                filterRequest.getStatusId()
                        )
                );
            }

            // Active Flag
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