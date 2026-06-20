package com.nec.middleware.rbacAuth.rbac.specification;

import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.ModuleListRequestDto;
import com.nec.middleware.rbacAuth.rbac.entity.RbacModule;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification builder for {@link RbacModule} list queries.
 */
public final class ModuleSpecification {

    private ModuleSpecification() {
    }

    /**
     * Builds a {@link Specification} from the module list request filters.
     *
     * @param request the list request DTO
     * @return specification for dynamic filtering
     */
    public static Specification<RbacModule> build(ModuleListRequestDto request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), RbacConstants.IS_DELETED_FALSE));

            if (request.getModuleId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("moduleId"), request.getModuleId()));
            }

            if (request.getModuleCode() != null && !request.getModuleCode().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("moduleCode")),
                                "%" + request.getModuleCode().toLowerCase() + "%"
                        )
                );
            }

            if (request.getModuleName() != null && !request.getModuleName().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("moduleName")),
                                "%" + request.getModuleName().toLowerCase() + "%"
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
                String searchTerm = "%" + request.getSearch().toLowerCase() + "%";
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("moduleCode")), searchTerm),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("moduleName")), searchTerm)
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
