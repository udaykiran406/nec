package com.nec.middleware.rbacAuth.rbac.specification;

import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.RoleListRequestDto;
import com.nec.middleware.rbacAuth.rbac.entity.RbacRole;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification builder for {@link RbacRole} list queries.
 */
public final class RoleSpecification {

    private RoleSpecification() {
    }

    /**
     * Builds a {@link Specification} from the role list request filters.
     *
     * @param request the list request DTO
     * @return specification for dynamic filtering
     */
    public static Specification<RbacRole> build(RoleListRequestDto request) {
        return (root, query, criteriaBuilder) -> {
            SpecificationUtil.fetchAssociations(query, root, "parentRole");

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), RbacConstants.IS_DELETED_FALSE));

            if (request.getRoleId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("roleId"), request.getRoleId()));
            }

            if (request.getParentRoleId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("parentRoleId"), request.getParentRoleId()));
            }

            if (request.getParentRoleName() != null && !request.getParentRoleName().isBlank()) {
                Join<?, ?> parentRoleJoin = root.join("parentRole", JoinType.INNER);
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(parentRoleJoin.get("roleName")),
                                SpecificationUtil.toLikePattern(request.getParentRoleName())
                        )
                );
            }

            if (request.getRoleCode() != null && !request.getRoleCode().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("roleCode")),
                                SpecificationUtil.toLikePattern(request.getRoleCode())
                        )
                );
            }

            if (request.getRoleName() != null && !request.getRoleName().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("roleName")),
                                SpecificationUtil.toLikePattern(request.getRoleName())
                        )
                );
            }

            if (request.getStatus() != null && !request.getStatus().isBlank()) {
                predicates.add(
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(root.get("status")),
                                request.getStatus().toLowerCase()
                        )
                );
            }

            if (request.getIsParentRole() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isParentRole"), request.getIsParentRole()));
            }

            if (request.getSearch() != null && !request.getSearch().isBlank()) {
                String searchTerm = SpecificationUtil.toLikePattern(request.getSearch());
                Join<?, ?> parentRoleJoin = root.join("parentRole", JoinType.LEFT);
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("roleCode")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("roleName")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(parentRoleJoin.get("roleName")), searchTerm)
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
