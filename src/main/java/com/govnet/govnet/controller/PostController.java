package com.govnet.govnet.controller;

import com.govnet.govnet.dto.PostRequestDTO;
import com.govnet.govnet.entity.Post;
import com.govnet.govnet.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.access.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestPart("data") PostRequestDTO dto,
            @RequestPart(value = "media", required = false) MultipartFile media,
            Principal principal
    ) {
        String username = principal.getName();
        Post post = postService.createPost(dto, media, username);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long postId,
            @RequestPart("data") PostRequestDTO dto,
            @RequestPart(value = "media", required = false) MultipartFile media,
            Principal principal
    ) {
        String username = principal.getName();
        Post updated = postService.updatePost(postId, dto, media, username);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, Principal principal) {
        String username = principal.getName();
        try {
            postService.deletePost(postId, username);
            return ResponseEntity.ok("Post deleted successfully");
        } catch (AccessDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}
