package com.govnet.govnet.controller;

import com.govnet.govnet.dto.PostRequestDTO;
import com.govnet.govnet.dto.PostResponseDTO;
import com.govnet.govnet.dto.UserMinimalDTO;
import com.govnet.govnet.entity.MyUser;
import com.govnet.govnet.repo.MyUserRepository;
import com.govnet.govnet.repo.PostRepository;
import com.govnet.govnet.repo.PostViewRepository;
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
    private PostViewRepository postViewRepository;
    private PostRepository postRepository;
    private MyUserRepository userRepository;

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @RequestPart("data") PostRequestDTO dto,
            @RequestPart(value = "media", required = false) MultipartFile media,
            Principal principal
    ) {
        String username = principal.getName();
        PostResponseDTO post = postService.createPost(dto, media, username);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable Long postId,
            @RequestPart("data") PostRequestDTO dto,
            @RequestPart(value = "media", required = false) MultipartFile media,
            Principal principal
    ) {
        String username = principal.getName();
        PostResponseDTO updated = postService.updatePost(postId, dto, media, username);
        return ResponseEntity.ok(updated);
    }

//    @GetMapping("/{postId}")
//    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long postId) {
//        PostResponseDTO post = postService.getPostById(postId);
//        return ResponseEntity.ok(post);
//    }


    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
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

//    @GetMapping("/{postId}/seen-users")
//    public ResponseEntity<List<UserMinimalDTO>> getSeenUsers(@PathVariable Long postId) {
//        List<MyUser> seenUsers = postService.getUsersWhoHaveSeen(postId);
//        List<UserMinimalDTO> response = seenUsers.stream()
//                .map(UserMinimalDTO::new)
//                .toList();
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/{postId}/unseen-users")
//    public ResponseEntity<List<UserMinimalDTO>> getUnseenUsers(@PathVariable Long postId) {
//        List<MyUser> unseenUsers = postService.getUsersWhoHaveNotSeen(postId);
//        List<UserMinimalDTO> response = unseenUsers.stream()
//                .map(UserMinimalDTO::new)
//                .toList();
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long postId, Principal principal) {
        String username = principal.getName();

        // ✅ Log the view
        postService.logPostView(postId, username);

        // ✅ Return the post response
        PostResponseDTO post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/{postId}/seen-users")
    public ResponseEntity<List<UserMinimalDTO>> getSeenUsers(@PathVariable Long postId) {
        List<MyUser> seenUsers = postService.getUsersWhoHaveSeen(postId);
        List<UserMinimalDTO> response = seenUsers.stream().map(UserMinimalDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}/unseen-users")
    public ResponseEntity<List<UserMinimalDTO>> getUnseenUsers(@PathVariable Long postId) {
        List<MyUser> unseenUsers = postService.getUsersWhoHaveNotSeen(postId);
        List<UserMinimalDTO> response = unseenUsers.stream().map(UserMinimalDTO::new).toList();
        return ResponseEntity.ok(response);
    }

}
