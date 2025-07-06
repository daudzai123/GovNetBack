package com.govnet.govnet.dto;

import lombok.Data;

@Data
public class DepartmentDTO {
    private Long depId;
    private String depName;
    private Long parentDepartmentId;
    private String description;
    public Long getDepId() {
        return depId;
    }

    public void setDepId(Long depId) {
        this.depId = depId;
    }

    public String getDepName() {
        return depName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public Long getParentDepartmentId() {
        return parentDepartmentId;
    }

    public void setParentDepartmentId(Long parentDepartmentId) {
        this.parentDepartmentId = parentDepartmentId;
    }
}
