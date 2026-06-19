package com.nec.middleware.hr.specification;

import com.nec.middleware.hr.dto.request.UniversityTraineeFilterRequestDto;
import com.nec.middleware.hr.entity.UniversityTrainee;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a JPA {@link Specification} for {@link UniversityTrainee} from
 * the filter fields in {@link UniversityTraineeFilterRequestDto}.
 * Mirrors {@code PortalUserSearchSpecification} exactly — all filters are
 * optional and null values are ignored.
 */
public class UniversityTraineeSearchSpecification {

    private UniversityTraineeSearchSpecification() {}

    public static Specification<UniversityTrainee> buildSpecification(
            UniversityTraineeFilterRequestDto request) {

        return (entityRoot, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // fullName — partial, case-insensitive (mirrors userName in PortalUser spec)
            if (request.getFullName() != null && !request.getFullName().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(entityRoot.get("fullName")),
                                "%" + request.getFullName().toLowerCase() + "%"
                        )
                );
            }

            // universityTraineeId — partial match (mirrors portalUserId in PortalUser spec)
            if (request.getUniversityTraineeId() != null &&
                    !request.getUniversityTraineeId().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                entityRoot.get("universityTraineeId"),
                                "%" + request.getUniversityTraineeId() + "%"
                        )
                );
            }

            // gender FK — traverse association (mirrors root.get("gender").get("id"))
            if (request.getGenderId() != null) {
                predicates.add(
                        criteriaBuilder.equal(entityRoot.get("gender").get("id"), request.getGenderId())
                );
            }

            // paymentMethod FK
            if (request.getPaymentMethodId() != null) {
                predicates.add(
                        criteriaBuilder.equal(entityRoot.get("paymentMethod").get("id"), request.getPaymentMethodId())
                );
            }

            // university FK
            if (request.getUniversityId() != null) {
                predicates.add(
                        criteriaBuilder.equal(entityRoot.get("university").get("id"), request.getUniversityId())
                );
            }

            // region FK
            if (request.getRegionId() != null) {
                predicates.add(
                        criteriaBuilder.equal(entityRoot.get("region").get("id"), request.getRegionId())
                );
            }

            // district FK
            if (request.getDistrictId() != null) {
                predicates.add(
                        criteriaBuilder.equal(entityRoot.get("district").get("id"), request.getDistrictId())
                );
            }

            // city FK
            if (request.getCityId() != null) {
                predicates.add(
                        criteriaBuilder.equal(entityRoot.get("city").get("id"), request.getCityId())
                );
            }

            // statusId — stored as plain String on the entity
            if (request.getStatusId() != null) {
                predicates.add(
                        criteriaBuilder.equal(entityRoot.get("statusId"), request.getStatusId())
                );
            }

            // isActive flag
            if (request.getIsActive() != null) {
                predicates.add(
                        criteriaBuilder.equal(entityRoot.get("isActive"), request.getIsActive())
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}