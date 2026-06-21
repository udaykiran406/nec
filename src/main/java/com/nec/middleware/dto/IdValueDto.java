package com.nec.middleware.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdValueDto {
    private Long   id;
    private String value;
}