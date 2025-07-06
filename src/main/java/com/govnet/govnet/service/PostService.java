package com.govnet.govnet.service;

import com.govnet.govnet.dto.PostRequestDTO;
import com.govnet.govnet.entity.Department;
import com.govnet.govnet.entity.MyUser;
import com.govnet.govnet.entity.Post;
import com.govnet.govnet.repo.MyUserRepository;
import com.govnet.govnet.repo.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MyUserRepository userRepository;
    private final FileStorageService fileStorageService;

    public Post createPost(PostRequestDTO dto, MultipartFile media, String email) {
        MyUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(user);
        List<Department> departments = user.getDepartment(); // assuming getDepartments()
        if (!departments.isEmpty()) {
            post.setDepartment(departments.get(0));
        }

        post.setUrgent(Boolean.TRUE.equals(dto.getIsUrgent()));

        if (media != null && !media.isEmpty()) {
            post.setMedia(fileStorageService.saveFile(media));
        }

        return postRepository.save(post);
    }

    public Post updatePost(Long postId, PostRequestDTO dto, MultipartFile media, String email) {
        MyUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("You are not the owner of this post.");
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setUrgent(Boolean.TRUE.equals(dto.getIsUrgent()));
        List<Department> departments = user.getDepartment(); // assuming getDepartments()
        if (!departments.isEmpty()) {
            post.setDepartment(departments.get(0));
        }

        if (media != null && !media.isEmpty()) {
            // Delete old media if exists
            if (post.getMedia() != null) {
                fileStorageService.deleteFile(post.getMedia());
            }
            // Save new media and update
            String newMediaPath = fileStorageService.saveFile(media);
            post.setMedia(newMediaPath);
        }

        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public void deletePost(Long postId, String email)  {
        MyUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this post.");
        }

        // Delete media file if exists
        if (post.getMedia() != null) {
            fileStorageService.deleteFile(post.getMedia());
        }

        postRepository.delete(post);
    }
}
