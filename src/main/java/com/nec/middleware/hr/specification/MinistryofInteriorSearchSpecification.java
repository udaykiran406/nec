package com.nec.middleware.hr.specification;

import com.nec.middleware.hr.dto.request.MinistryofInteriorFilterRequestDto;
import com.nec.middleware.hr.entity.MinistryofInterior;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MinistryofInteriorSearchSpecification {

    private MinistryofInteriorSearchSpecification() {
    }

    public static Specification<MinistryofInterior> buildSpecification(
            MinistryofInteriorFilterRequestDto filterRequest) {

        return (entityRoot, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> filterPredicates = new ArrayList<>();

            // Aaqil ID
            if (filterRequest.getMinistryofInteriorId() != null
                    && !filterRequest.getMinistryofInteriorId().isBlank()) {

                filterPredicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        entityRoot.get("MinistryofInteriorId")),
                                "%" + filterRequest.getMinistryofInteriorId()
                                        .toLowerCase() + "%"
                        )
                );
            }

            //  Name
            if (filterRequest.getName() != null
                    && !filterRequest.getName().isBlank()) {

                filterPredicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        entityRoot.get("Name")),
                                "%" + filterRequest.getName()
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



            // Ministry of Interior Title
            if (filterRequest.getMoiTitleId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("moiTitle").get("id"),
                                filterRequest.getMoiTitleId()
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

            // Voter Registration Center
            if (filterRequest.getVrcId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("vrc").get("id"),
                                filterRequest.getVrcId()
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