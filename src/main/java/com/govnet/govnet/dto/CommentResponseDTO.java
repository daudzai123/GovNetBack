package com.govnet.govnet.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {

    private Long id;
    private String content;
    private LocalDateTime createdAt;

    private Long authorId;
    private String authorName;
    private String authorProfileImage;

    private Long postId;
}
