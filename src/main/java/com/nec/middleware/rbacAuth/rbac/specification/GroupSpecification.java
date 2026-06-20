package com.nec.middleware.rbacAuth.rbac.specification;

import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.GroupListRequestDto;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermissionGroup;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification builder for {@link RbacPermissionGroup} list queries.
 */
public final class GroupSpecification {

    private GroupSpecification() {
    }

    /**
     * Builds a {@link Specification} from the group list request filters.
     *
     * @param request the list request DTO
     * @return specification for dynamic filtering
     */
    public static Specification<RbacPermissionGroup> build(GroupListRequestDto request) {
        return (root, query, criteriaBuilder) -> {
            SpecificationUtil.fetchAssociations(query, root, "module");

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), RbacConstants.IS_DELETED_FALSE));

            if (request.getGroupId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("groupId"), request.getGroupId()));
            }

            if (request.getModuleId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("moduleId"), request.getModuleId()));
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
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("groupCode")),
                                SpecificationUtil.toLikePattern(request.getGroupCode())
                        )
                );
            }

            if (request.getGroupName() != null && !request.getGroupName().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("groupName")),
                                SpecificationUtil.toLikePattern(request.getGroupName())
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

            if (request.getSearch() != null && !request.getSearch().isBlank()) {
                String searchTerm = SpecificationUtil.toLikePattern(request.getSearch());
                Join<?, ?> moduleJoin = root.join("module", JoinType.LEFT);
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("groupCode")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("groupName")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(moduleJoin.get("moduleCode")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(moduleJoin.get("moduleName")), searchTerm)
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
