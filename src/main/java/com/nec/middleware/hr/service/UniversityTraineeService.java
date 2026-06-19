package com.nec.middleware.hr.service;


import com.nec.middleware.hr.dto.request.UniversityTraineeFilterRequestDto;
import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;


public interface UniversityTraineeService {
    UniversityTraineeResponseDto createTrainee(UniversityTraineeRequestDto requestDto,MultipartFile photo);

    UniversityTraineeResponseDto getTraineeByUniversityTraineeId(String universityTraineeId);

    Page<UniversityTraineeResponseDto> getAllUniversityTrainees(UniversityTraineeFilterRequestDto filterDto, int pageNumber, int pageSize);

    UniversityTraineeResponseDto changeStatus(String universityTraineeId,Boolean isActive);

    UniversityTraineeResponseDto updateTrainee(String universityTraineeId, UniversityTraineeRequestDto request, MultipartFile photo);
}