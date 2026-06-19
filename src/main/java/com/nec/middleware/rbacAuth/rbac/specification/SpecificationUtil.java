package com.nec.middleware.rbacAuth.rbac.specification;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;

/**
 * Shared helpers for JPA {@link org.springframework.data.jpa.domain.Specification} builders.
 */
public final class SpecificationUtil {

    private SpecificationUtil() {
    }

    /**
     * Applies LEFT fetch joins for list queries to avoid N+1 lazy loading.
     * Skips fetch joins for count queries used by pagination.
     *
     * @param query       the criteria query
     * @param root        the query root
     * @param associations association attribute names to fetch
     */
    public static void fetchAssociations(CriteriaQuery<?> query, From<?, ?> root, String... associations) {
        if (isCountQuery(query)) {
            return;
        }
        for (String association : associations) {
            root.fetch(association, JoinType.LEFT);
        }
        query.distinct(true);
    }

    /**
     * Returns a case-insensitive LIKE pattern for partial text matching.
     *
     * @param value the raw search value
     * @return lowercase pattern wrapped with wildcards
     */
    public static String toLikePattern(String value) {
        return "%" + value.toLowerCase() + "%";
    }

    private static boolean isCountQuery(CriteriaQuery<?> query) {
        Class<?> resultType = query.getResultType();
        return resultType == Long.class || resultType == long.class;
    }
}
