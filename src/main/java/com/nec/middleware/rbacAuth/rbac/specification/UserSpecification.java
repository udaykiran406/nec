package com.nec.middleware.rbacAuth.rbac.specification;

import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.UserListRequestDto;
import com.nec.middleware.rbacAuth.rbac.entity.RbacUser;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification builder for {@link RbacUser} list queries.
 */
public final class UserSpecification {

    private UserSpecification() {
    }

    /**
     * Builds a {@link Specification} from the user list request filters.
     *
     * @param request the list request DTO
     * @return specification for dynamic filtering
     */
    public static Specification<RbacUser> build(UserListRequestDto request) {
        return (root, query, criteriaBuilder) -> {
            SpecificationUtil.fetchAssociations(
                    query, root, "role", "gender", "department", "region", "district", "city");

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), RbacConstants.IS_DELETED_FALSE));

            if (request.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), request.getUserId()));
            }

            if (request.getRoleId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("roleId"), request.getRoleId()));
            }

            if (request.getRoleCode() != null && !request.getRoleCode().isBlank()) {
                Join<?, ?> roleJoin = root.join("role", JoinType.INNER);
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(roleJoin.get("roleCode")),
                                SpecificationUtil.toLikePattern(request.getRoleCode())
                        )
                );
            }

            if (request.getRoleName() != null && !request.getRoleName().isBlank()) {
                Join<?, ?> roleJoin = root.join("role", JoinType.INNER);
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(roleJoin.get("roleName")),
                                SpecificationUtil.toLikePattern(request.getRoleName())
                        )
                );
            }

            if (request.getGenderId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("genderId"), request.getGenderId()));
            }

            if (request.getDepartmentId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("departmentId"), request.getDepartmentId()));
            }

            if (request.getRegionId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("regionId"), request.getRegionId()));
            }

            if (request.getDistrictId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("districtId"), request.getDistrictId()));
            }

            if (request.getCityId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("cityId"), request.getCityId()));
            }

            if (request.getUserName() != null && !request.getUserName().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("userName")),
                                SpecificationUtil.toLikePattern(request.getUserName())
                        )
                );
            }

            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("email")),
                                SpecificationUtil.toLikePattern(request.getEmail())
                        )
                );
            }

            if (request.getMobileNumber() != null && !request.getMobileNumber().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                root.get("phone"),
                                "%" + request.getMobileNumber() + "%"
                        )
                );
            }

            if (request.getIsActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), request.getIsActive()));
            }

            if (request.getSearch() != null && !request.getSearch().isBlank()) {
                String searchTerm = SpecificationUtil.toLikePattern(request.getSearch());
                Join<?, ?> roleJoin = root.join("role", JoinType.LEFT);
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("userName")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchTerm),
                                criteriaBuilder.like(root.get("phone"), "%" + request.getSearch() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(roleJoin.get("roleName")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(roleJoin.get("roleCode")), searchTerm)
                        )
                );
            }

            if (request.getFromDate() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                request.getFromDate().atStartOfDay()
                        )
                );
            }

            if (request.getToDate() != null) {
                predicates.add(
                        criteriaBuilder.lessThan(
                                root.get("createdAt"),
                                request.getToDate().plusDays(1).atStartOfDay()
                        )
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
