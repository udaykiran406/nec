package com.nec.middleware.rbacAuth.rbac.specification;

import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.PermissionListRequestDto;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermission;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification builder for {@link RbacPermission} list queries.
 */
public final class PermissionSpecification {

    private PermissionSpecification() {
    }

    /**
     * Builds a {@link Specification} from the permission list request filters.
     *
     * @param request the list request DTO
     * @return specification for dynamic filtering
     */
    public static Specification<RbacPermission> build(PermissionListRequestDto request) {
        return (root, query, criteriaBuilder) -> {
            SpecificationUtil.fetchAssociations(query, root, "module", "group");

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), RbacConstants.IS_DELETED_FALSE));

            if (request.getPermissionId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("permissionId"), request.getPermissionId()));
            }

            if (request.getModuleId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("moduleId"), request.getModuleId()));
            }

            if (request.getGroupId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("groupId"), request.getGroupId()));
            }

            if (request.getModuleCode() != null && !request.getModuleCode().isBlank()) {
                Join<?, ?> moduleJoin = root.join("module", JoinType.INNER);
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(moduleJoin.get("moduleCode")),
                                SpecificationUtil.toLikePattern(request.getModuleCode())
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

            if (request.getGroupCode() != null && !request.getGroupCode().isBlank()) {
                Join<?, ?> groupJoin = root.join("group", JoinType.INNER);
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(groupJoin.get("groupCode")),
                                SpecificationUtil.toLikePattern(request.getGroupCode())
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

            if (request.getPermissionCode() != null && !request.getPermissionCode().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("permissionCode")),
                                SpecificationUtil.toLikePattern(request.getPermissionCode())
                        )
                );
            }

            if (request.getPermissionName() != null && !request.getPermissionName().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("permissionName")),
                                SpecificationUtil.toLikePattern(request.getPermissionName())
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

            if (request.getIsSideMenu() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isSideMenu"), request.getIsSideMenu()));
            }

            if (request.getSearch() != null && !request.getSearch().isBlank()) {
                String searchTerm = SpecificationUtil.toLikePattern(request.getSearch());
                Join<?, ?> moduleJoin = root.join("module", JoinType.LEFT);
                Join<?, ?> groupJoin = root.join("group", JoinType.LEFT);
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("permissionCode")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("permissionName")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(moduleJoin.get("moduleCode")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(moduleJoin.get("moduleName")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(groupJoin.get("groupCode")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(groupJoin.get("groupName")), searchTerm)
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
