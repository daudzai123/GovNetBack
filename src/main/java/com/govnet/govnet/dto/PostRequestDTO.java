package com.govnet.govnet.dto;

import lombok.Data;

@Data
public class PostRequestDTO {
    private String title;
    private String content;
    private Boolean isUrgent;
}
