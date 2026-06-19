package com.nec.middleware.rbacAuth.rbac.util;

import com.nec.middleware.rbacAuth.rbac.dto.response.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility methods for building pagination, sorting, and paginated responses
 * in RBAC list APIs.
 */
public final class RbacPaginationUtil {

    private RbacPaginationUtil() {
        throw new UnsupportedOperationException("RbacPaginationUtil is a utility class");
    }

    /**
     * Returns {@code true} when both page and size are provided for paginated fetch.
     *
     * @param page zero-based page number
     * @param size page size
     * @return {@code true} if pagination should be applied
     */
    public static boolean isPaginationRequested(Integer page, Integer size) {
        return page != null && size != null;
    }

    /**
     * Builds a {@link Sort} from request parameters with entity-specific default field and DESC direction.
     *
     * @param sortBy            optional sort field name
     * @param sortDirection     optional sort direction (ASC or DESC)
     * @param defaultSortField  default field when sortBy is not provided
     * @return configured Sort instance
     */
    public static Sort buildSort(String sortBy, String sortDirection, String defaultSortField) {
        String field = RbacUtil.isNotBlank(sortBy) ? sortBy.trim() : defaultSortField;
        Sort.Direction direction = Sort.Direction.DESC;
        if (RbacUtil.isNotBlank(sortDirection) && "ASC".equalsIgnoreCase(sortDirection.trim())) {
            direction = Sort.Direction.ASC;
        }
        return Sort.by(direction, field);
    }

    /**
     * Creates a {@link Pageable} when pagination is requested.
     *
     * @param page zero-based page number
     * @param size page size
     * @param sort sort configuration
     * @return PageRequest instance
     */
    public static Pageable buildPageable(Integer page, Integer size, Sort sort) {
        int safePage = page != null && page >= 0 ? page : 0;
        int safeSize = size != null && size > 0 ? size : 10;
        return PageRequest.of(safePage, safeSize, sort);
    }

    /**
     * Converts a Spring Data {@link Page} of entities into a {@link PaginatedResponse} of DTOs.
     *
     * @param page   the page result from the repository
     * @param mapper entity-to-DTO mapper
     * @param <E>    entity type
     * @param <R>    response DTO type
     * @return paginated response DTO
     */
    public static <E, R> PaginatedResponse<R> fromPage(Page<E> page, Function<E, R> mapper) {
        List<R> content = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());
        return PaginatedResponse.<R>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    /**
     * Wraps a full list result (non-paginated fetch) in {@link PaginatedResponse} format.
     *
     * @param content list of response DTOs
     * @param <R>     response DTO type
     * @return paginated-style response with a single logical page
     */
    public static <R> PaginatedResponse<R> fromList(List<R> content) {
        int total = content.size();
        return PaginatedResponse.<R>builder()
                .content(content)
                .page(0)
                .size(total)
                .totalElements((long) total)
                .totalPages(total == 0 ? 0 : 1)
                .first(true)
                .last(true)
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }
}

