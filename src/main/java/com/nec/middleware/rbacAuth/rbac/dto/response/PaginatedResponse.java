package com.nec.middleware.rbacAuth.rbac.dto.response;

import lombok.*;

import java.util.List;

/**
 * Generic paginated response wrapper used by RBAC list APIs.
 *
 * <p>Sample JSON:
 * <pre>
 * {
 *   "content": [],
 *   "page": 0,
 *   "size": 10,
 *   "totalElements": 100,
 *   "totalPages": 10,
 *   "first": true,
 *   "last": false,
 *   "hasNext": true,
 *   "hasPrevious": false
 * }
 * </pre>
 *
 * @param <T> the type of items in the content list
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponse<T> {

    /** List of records for the current page or full result set. */
    private List<T> content;

    /** Zero-based page number. */
    private Integer page;

    /** Number of records per page. */
    private Integer size;

    /** Total number of matching records. */
    private Long totalElements;

    /** Total number of pages. */
    private Integer totalPages;

    /** Whether this is the first page. */
    private Boolean first;

    /** Whether this is the last page. */
    private Boolean last;

    /** Whether a next page exists. */
    private Boolean hasNext;

    /** Whether a previous page exists. */
    private Boolean hasPrevious;
}
