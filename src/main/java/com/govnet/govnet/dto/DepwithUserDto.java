package com.govnet.govnet.dto;

public class DepwithUserDto {
    private Long depId;
    private String depName;

    public DepwithUserDto(Long depId2, String depName2) {
        this.depId = depId2;
        this.depName = depName2;
    }

    public Long getDepId() {
        return depId;
    }

    public String getDepName() {
        return depName;
    }
}
