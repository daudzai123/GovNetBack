package com.govnet.govnet.controller;

import com.govnet.govnet.dto.CommentRequestDTO;
import com.govnet.govnet.dto.CommentResponseDTO;
import com.govnet.govnet.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 🔹 Create a new comment
    @PostMapping
    public ResponseEntity<CommentResponseDTO> addComment(@RequestBody CommentRequestDTO dto, Principal principal) {
        String username = principal.getName();
        CommentResponseDTO saved = commentService.addComment(dto, username);
        return ResponseEntity.ok(saved);
    }

    // 🔹 Get all comments for a specific post
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsForPost(@PathVariable Long postId) {
        List<CommentResponseDTO> comments = commentService.getCommentsForPost(postId);
        return ResponseEntity.ok(comments);
    }

    // 🔹 Get a single comment by ID
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable Long id) {
        CommentResponseDTO comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    // 🔹 Update a comment (only author can update)
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long id,
            @RequestBody CommentRequestDTO dto,
            Principal principal
    ) throws AccessDeniedException {
        String username = principal.getName();
        CommentResponseDTO updated = commentService.updateComment(id, dto, username);
        return ResponseEntity.ok(updated);
    }

    // 🔹 Delete a comment (only author can delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id, Principal principal) throws AccessDeniedException {
        String username = principal.getName();
        commentService.deleteComment(id, username);
        return ResponseEntity.ok("Comment deleted successfully");
    }
}
