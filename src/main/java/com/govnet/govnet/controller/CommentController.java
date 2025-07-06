package com.govnet.govnet.controller;

import com.govnet.govnet.dto.CommentRequestDTO;
import com.govnet.govnet.entity.Comment;
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

    // Create a comment
    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody CommentRequestDTO dto, Principal principal) {
        String username = principal.getName();
        Comment saved = commentService.addComment(dto, username);
        return ResponseEntity.ok(saved);
    }

    // Get all comments for a post
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsForPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsForPost(postId);
        return ResponseEntity.ok(comments);
    }

    // Get comment by id
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    // Update comment - only author can update
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long id,
            @RequestBody CommentRequestDTO dto,
            Principal principal
    ) throws AccessDeniedException {
        String username = principal.getName();
        Comment updated = commentService.updateComment(id, dto, username);
        return ResponseEntity.ok(updated);
    }

    // Delete comment - only author can delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id, Principal principal) throws AccessDeniedException {
        String username = principal.getName();
        commentService.deleteComment(id, username);
        return ResponseEntity.ok("Comment deleted successfully");
    }
}
