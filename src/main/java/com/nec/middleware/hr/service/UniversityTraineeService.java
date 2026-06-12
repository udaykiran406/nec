package com.nec.middleware.hr.service;


import com.nec.middleware.hr.dto.request.PortalUserListRequestDto;
import com.nec.middleware.hr.dto.request.PortalUserRequestDto;
import com.nec.middleware.hr.dto.request.UniversityTraineeFilterRequestDto;
import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.PortalUserResponseDto;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import org.springframework.data.domain.Page;


public interface UniversityTraineeService {
    UniversityTraineeResponseDto createTrainee(UniversityTraineeRequestDto requestDto);

    UniversityTraineeResponseDto getTraineeByUniversityTraineeId(String universityTraineeId);

    Page<UniversityTraineeResponseDto> getAllUniversityTrainees(UniversityTraineeFilterRequestDto filterDto, int pageNumber, int pageSize);

    UniversityTraineeResponseDto changeStatus(String universityTraineeId,Boolean isActive);

    UniversityTraineeResponseDto updateTrainee(String universityTraineeId, UniversityTraineeRequestDto request);
}