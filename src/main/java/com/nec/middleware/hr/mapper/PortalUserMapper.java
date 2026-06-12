package com.nec.middleware.hr.mapper;

import com.nec.middleware.hr.dto.request.PortalUserRequestDto;
import com.nec.middleware.hr.dto.response.IdValueDto;
import com.nec.middleware.hr.dto.response.PortalUserResponseDto;
import com.nec.middleware.hr.entity.PortalUser;

import org.springframework.stereotype.Component;

@Component
public class PortalUserMapper {

    /**
     * Map RequestDto → new Entity (for CREATE)
     */
    public PortalUser portalUserEntity(PortalUserRequestDto dto) {
        PortalUser portalUserEntity = PortalUser.builder()
                .userName(dto.getUserName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .photoPath(dto.getPhotoPath())
                .faculty(dto.getFaculty())
                .build();

        portalUserEntity.setIsActive(Boolean.TRUE);
        portalUserEntity.setCreatedBy(dto.getCreatedBy());
        portalUserEntity.setUpdatedBy(dto.getCreatedBy());

        return portalUserEntity;
    }

    /**
     * Merge RequestDto → existing Entity (for UPDATE)
     */
    public void updatePortalUserEntity(PortalUser portalUser, PortalUserRequestDto portalUserRequestDto) {

        if (portalUserRequestDto.getUserName() != null) {
            portalUser.setUserName(portalUserRequestDto.getUserName());
        }

        if (portalUserRequestDto.getPhone() != null) {
            portalUser.setPhone(portalUserRequestDto.getPhone());
        }

        if (portalUserRequestDto.getEmail() != null) {
            portalUser.setEmail(portalUserRequestDto.getEmail());
        }

        if (portalUserRequestDto.getPhotoPath() != null) {
            portalUser.setPhotoPath(portalUserRequestDto.getPhotoPath());
        }

        if (portalUserRequestDto.getFaculty() != null) {
            portalUser.setFaculty(portalUserRequestDto.getFaculty());
        }
    }


    // ------------------------------------------------------------------
    // READ  — Entity → ResponseDto
    // ------------------------------------------------------------------

    /**
     * Maps every FK association to an {@link IdValueDto} so the response
     * carries both the id and the human-readable label:
     * <pre>
     *   "city":  { "id": 1, "value": "Hyderabad" },
     *   "role":  { "id": 2, "value": "Admin"     }
     * </pre>
     */
    public PortalUserResponseDto portalUserResponseDto(PortalUser entity) {
        return PortalUserResponseDto.builder()
                // Identity
                .id(entity.getId())
                .portalUserId(entity.getPortalUserId())
                .userName(entity.getUserName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .photoPath(entity.getPhotoPath())
                .faculty(entity.getFaculty())

                // Lookup FKs → IdValueDto
                .gender(entity.getGender() != null
                        ? IdValueDto.builder()
                          .id(entity.getGender().getId())
                          .value(entity.getGender().getValue())
                          .build()
                        : null)

                .role(entity.getRole() != null
                        ? IdValueDto.builder()
                          .id(entity.getRole().getId())
                          .value(entity.getRole().getValue())
                          .build()
                        : null)

                .portalUserType(entity.getPortalUserType() != null
                        ? IdValueDto.builder()
                          .id(entity.getPortalUserType().getId())
                          .value(entity.getPortalUserType().getValue())
                          .build()
                        : null)

                .region(entity.getRegion() != null
                        ? IdValueDto.builder()
                          .id(entity.getRegion().getId())
                          .value(entity.getRegion().getRegionName())
                          .build()
                        : null)

                .district(entity.getDistrict() != null
                        ? IdValueDto.builder()
                          .id(entity.getDistrict().getId())
                          .value(entity.getDistrict().getDistrictName())
                          .build()
                        : null)

                .city(entity.getCity() != null
                        ? IdValueDto.builder()
                          .id(entity.getCity().getId())
                          .value(entity.getCity().getCityName())
                          .build()
                        : null)

                // Audit
                .isActive(entity.getIsActive())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
