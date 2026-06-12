package com.nec.middleware.hr.dto.response;

import lombok.*;

/**
 * Generic id + value wrapper used in every PortalUser (and other) response DTOs
 * so that FK fields are returned as objects instead of flat strings.
 *
 * Example JSON:
 * <pre>
 * "city":  { "id": 1, "value": "Hyderabad" },
 * "role":  { "id": 2, "value": "Admin"     }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdValueDto {
    private Long   id;
    private String value;
}