package com.nec.middleware.hr.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Every lookup and master-data FK is returned as an {@link IdValueDto}:
 *
 * <pre>
 * "gender":        { "id": 1, "value": "Male"      },
 * "paymentMethod": { "id": 2, "value": "Cash"       },
 * "university":    { "id": 3, "value": "UOK"        },
 * "region":        { "id": 4, "value": "Mogadishu"  },
 * "district":      { "id": 5, "value": "Hodan"      },
 * "city":          { "id": 6, "value": "Hyderabad"  }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniversityTraineeResponseDto {

    // ------------------------------------------------------------------ Identity
    private Long   id;

    /** Business key — e.g. UT001. Mirrors portalUserId in PortalUserResponseDto. */
    private String universityTraineeId;

    private String fullName;
    private Short  age;
    private String phone;
    private String email;
    private String semester;
    private String faculty;
    private String photoUrl;
    private Long   statusId;

    // ------------------------------------------------------------------ Lookup FKs → IdValueDto
    private IdValueDto gender;
    private IdValueDto paymentMethod;

    // ------------------------------------------------------------------ Master Data FKs → IdValueDto
    private IdValueDto university;
    private IdValueDto region;
    private IdValueDto district;
    private IdValueDto city;

    // ------------------------------------------------------------------ Audit
    private Boolean       isActive;
    private Boolean       isDeleted;
    private String        createdBy;
    private LocalDateTime createdAt;
    private String        updatedBy;
    private LocalDateTime updatedAt;
}