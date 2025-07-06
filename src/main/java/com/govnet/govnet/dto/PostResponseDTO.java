package com.govnet.govnet.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String media;
    private boolean urgent;
    private LocalDateTime createdAt;
    private String authorFullName;
    private List<String> visibleDepartments;
}