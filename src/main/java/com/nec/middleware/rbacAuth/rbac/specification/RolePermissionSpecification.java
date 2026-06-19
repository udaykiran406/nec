package com.nec.middleware.rbacAuth.rbac.specification;

import com.nec.middleware.rbacAuth.rbac.dto.request.RolePermissionListRequestDto;
import com.nec.middleware.rbacAuth.rbac.entity.RbacRolePermission;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification builder for {@link RbacRolePermission} list queries.
 */
public final class RolePermissionSpecification {

    private RolePermissionSpecification() {
    }

    /**
     * Builds a {@link Specification} from the role-permission list request filters.
     *
     * @param request the list request DTO
     * @return specification for dynamic filtering
     */
    public static Specification<RbacRolePermission> build(RolePermissionListRequestDto request) {
        return (root, query, criteriaBuilder) -> {
            SpecificationUtil.fetchAssociations(query, root, "role", "module", "group", "permission");

            List<Predicate> predicates = new ArrayList<>();

            if (request.getRolePermissionId() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("rolePermissionId"), request.getRolePermissionId()));
            }

            if (request.getRoleId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("roleId"), request.getRoleId()));
            }

            if (request.getModuleId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("moduleId"), request.getModuleId()));
            }

            if (request.getGroupId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("groupId"), request.getGroupId()));
            }

            if (request.getPermissionId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("permissionId"), request.getPermissionId()));
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

            if (request.getModuleName() != null && !request.getModuleName().isBlank()) {
                Join<?, ?> moduleJoin = root.join("module", JoinType.INNER);
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(moduleJoin.get("moduleName")),
                                SpecificationUtil.toLikePattern(request.getModuleName())
                        )
                );
            }

            if (request.getGroupName() != null && !request.getGroupName().isBlank()) {
                Join<?, ?> groupJoin = root.join("group", JoinType.INNER);
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(groupJoin.get("groupName")),
                                SpecificationUtil.toLikePattern(request.getGroupName())
                        )
                );
            }

            if (request.getPermissionName() != null && !request.getPermissionName().isBlank()) {
                Join<?, ?> permissionJoin = root.join("permission", JoinType.INNER);
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(permissionJoin.get("permissionName")),
                                SpecificationUtil.toLikePattern(request.getPermissionName())
                        )
                );
            }

            if (request.getStatus() != null && !request.getStatus().isBlank()) {
                predicates.add(
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(root.get("status")),
                                request.getStatus().toLowerCase()));
            }

            if (request.getSearch() != null && !request.getSearch().isBlank()) {
                String searchTerm = SpecificationUtil.toLikePattern(request.getSearch());
                Join<?, ?> roleJoin = root.join("role", JoinType.LEFT);
                Join<?, ?> moduleJoin = root.join("module", JoinType.LEFT);
                Join<?, ?> groupJoin = root.join("group", JoinType.LEFT);
                Join<?, ?> permissionJoin = root.join("permission", JoinType.LEFT);
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(roleJoin.get("roleName")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(roleJoin.get("roleCode")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(moduleJoin.get("moduleName")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(groupJoin.get("groupName")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(permissionJoin.get("permissionName")), searchTerm)
                        )
                );
            }

            if (request.getFromDate() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("createdDate"),
                                request.getFromDate().atStartOfDay()));
            }

            if (request.getToDate() != null) {
                predicates.add(
                        criteriaBuilder.lessThan(
                                root.get("createdDate"),
                                request.getToDate().plusDays(1).atStartOfDay()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
