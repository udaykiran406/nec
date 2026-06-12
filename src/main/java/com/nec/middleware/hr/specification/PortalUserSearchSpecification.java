package com.nec.middleware.hr.specification;

import com.nec.middleware.hr.dto.request.PortalUserListRequestDto;
import com.nec.middleware.hr.entity.PortalUser;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class PortalUserSearchSpecification {

    private PortalUserSearchSpecification() {
    }

    public static Specification<PortalUser> build(
            PortalUserListRequestDto request) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getUserName() != null &&
                    !request.getUserName().isBlank()) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("userName")),
                                "%" + request.getUserName().toLowerCase() + "%"
                        )
                );
            }

            if (request.getPortalUserId() != null &&
                    !request.getPortalUserId().isBlank()) {

                predicates.add(
                        criteriaBuilder.like(
                                root.get("portalUserId"),
                                "%" + request.getPortalUserId() + "%"
                        )
                );
            }

            if (request.getGenderId() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("gender").get("id"),
                                request.getGenderId())
                );
            }

            if (request.getRoleId() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("role").get("id"),
                                request.getRoleId())
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

            if (request.getPortalUserTypeId() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("portalUserType").get("id"),
                                request.getPortalUserTypeId())
                );
            }

            if (request.getIsActive() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("isActive"),
                                request.getIsActive())
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0]));
        };
    }
}