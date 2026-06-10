package com.nec.middleware.hr.service;


import com.nec.middleware.hr.dto.request.UniversityTraineeListRequestDto;
import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import org.springframework.data.domain.Page;


public interface UniversityTraineeService {

    UniversityTraineeResponseDto saveOrUpdate(
            UniversityTraineeRequestDto requestDto);



    UniversityTraineeResponseDto getById(Long id);

    Page<UniversityTraineeResponseDto> getAll(UniversityTraineeListRequestDto filterDto);

    UniversityTraineeResponseDto changeStatus(Long id);


    void softDelete(Long id);
}