package com.nec.middleware.hr.dto.response;

import com.nec.middleware.hr.Enum.MasterData;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Every lookup and master-data FK is returned as an {@link IdValueDto}
 * so the front-end receives both the id and the human-readable label
 * in a structured object:
 *
 * <pre>
 * "gender":         { "id": 1, "value": "Male"       },
 * "role":           { "id": 2, "value": "Admin"       },
 * "portalUserType": { "id": 3, "value": "Staff"       },
 * "university":     { "id": 4, "value": "UOK"         },
 * "region":         { "id": 5, "value": "Mogadishu"   },
 * "district":       { "id": 6, "value": "Hodan"       },
 * "city":           { "id": 7, "value": "Hyderabad"   }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortalUserResponseDto {

    // ------------------------------------------------------------------ Identity
    private Long   id;
    private String portalUserId;
    private String userName;
    private String phone;
    private String email;
    private String photoPath;
    private String faculty;

    // ------------------------------------------------------------------ Lookup FKs → IdValueDto
    /** nec_lkp_genders */
    private IdValueDto gender;

    /** nec_lkp_roles */
    private IdValueDto role;

    /** nec_lkp_portal_user_types */
    private IdValueDto portalUserType;

    // ------------------------------------------------------------------ Master Data FKs → IdValueDto
    private IdValueDto masterData;


    /** nec_regions */
    private IdValueDto region;

    /** nec_districts */
    private IdValueDto district;

    /** nec_cities */
    private IdValueDto city;

    // ------------------------------------------------------------------ Audit
    private Boolean       isActive;
    private Boolean       isDeleted;
    private String        createdBy;
    private LocalDateTime createdAt;
    private String        updatedBy;
    private LocalDateTime updatedAt;
}