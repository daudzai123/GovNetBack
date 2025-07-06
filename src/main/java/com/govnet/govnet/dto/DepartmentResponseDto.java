package com.govnet.govnet.dto;

import lombok.Data;

@Data
public class DepartmentResponseDto {
    private Long depId;
    private String depName;
    private String description;
    private DepwithUserDto parent;

}
