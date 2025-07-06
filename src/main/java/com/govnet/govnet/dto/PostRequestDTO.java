package com.govnet.govnet.dto;

import lombok.Data;
import java.util.List;

@Data
public class PostRequestDTO {
    private String title;
    private String content;
    private Boolean isUrgent;
    private List<Long> visibleDepartmentIds;
}
