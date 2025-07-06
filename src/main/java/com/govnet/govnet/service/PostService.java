package com.govnet.govnet.service;

import com.govnet.govnet.dto.PostRequestDTO;
import com.govnet.govnet.dto.PostResponseDTO;
import com.govnet.govnet.entity.Department;
import com.govnet.govnet.entity.MyUser;
import com.govnet.govnet.entity.Post;
import com.govnet.govnet.entity.PostView;
import com.govnet.govnet.repo.DepartmentRepository;
import com.govnet.govnet.repo.MyUserRepository;
import com.govnet.govnet.repo.PostRepository;
import com.govnet.govnet.repo.PostViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MyUserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final FileStorageService fileStorageService;
    private final CurrentUserInfoService currentUserInfoService;
    private final PostViewRepository postViewRepository;

    public PostResponseDTO createPost(PostRequestDTO dto, MultipartFile media, String email) {
        MyUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(user);
        post.setUrgent(Boolean.TRUE.equals(dto.getIsUrgent()));

        if (dto.getVisibleDepartmentIds() != null) {
            List<Department> visibleDeps = dto.getVisibleDepartmentIds().stream()
                    .map(this::getDepartment)
                    .collect(Collectors.toList());
            post.setVisibleDepartments(visibleDeps);
        }

        if (media != null && !media.isEmpty()) {
            post.setMedia(fileStorageService.saveFile(media));
        }

        return convertToDTO(postRepository.save(post));
    }

    public PostResponseDTO updatePost(Long postId, PostRequestDTO dto, MultipartFile media, String email) {
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

        if (dto.getVisibleDepartmentIds() != null) {
            List<Department> visibleDeps = dto.getVisibleDepartmentIds().stream()
                    .map(this::getDepartment)
                    .collect(Collectors.toList());
            post.setVisibleDepartments(visibleDeps);
        }

        if (media != null && !media.isEmpty()) {
            if (post.getMedia() != null) fileStorageService.deleteFile(post.getMedia());
            post.setMedia(fileStorageService.saveFile(media));
        }

        return convertToDTO(postRepository.save(post));
    }

    public void deletePost(Long postId, String email) {
        MyUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to delete this post.");
        }

        if (post.getMedia() != null) fileStorageService.deleteFile(post.getMedia());
        postRepository.delete(post);
    }

    public PostResponseDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return convertToDTO(post);
    }

    public List<PostResponseDTO> getAllPosts() {
        MyUser user = currentUserInfoService.getCurrentUser();
        List<Department> userDepartments = user.getDepartment();

        return postRepository.findAll().stream()
                .filter(post -> post.getVisibleDepartments().stream()
                        .anyMatch(userDepartments::contains))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private Department getDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id " + id));
    }

    public void logPostView(Long postId, String username) {
        MyUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean alreadyViewed = postViewRepository.existsByPostAndUser(post, user);
        if (!alreadyViewed) {
            PostView view = new PostView();
            view.setPost(post);
            view.setUser(user);
            view.setViewedAt(LocalDateTime.now());
            postViewRepository.save(view);
        }
    }

    public List<MyUser> getUsersWhoHaveSeen(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return postViewRepository.findByPost(post).stream()
                .map(PostView::getUser)
                .distinct() // ensure unique users
                .toList();
    }

    public List<MyUser> getUsersWhoHaveNotSeen(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // All users from visible departments
        List<MyUser> allowedUsers = userRepository.findAll().stream()
                .filter(user -> {
                    if (user.getDepartment() == null || user.getDepartment().isEmpty()) return false;
                    return user.getDepartment().stream()
                            .anyMatch(dep -> post.getVisibleDepartments().contains(dep));
                })
                .toList();

        List<MyUser> seenUsers = getUsersWhoHaveSeen(postId);

        return allowedUsers.stream()
                .filter(user -> !seenUsers.contains(user))
                .toList();
    }



    private PostResponseDTO convertToDTO(Post post) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setMedia(post.getMedia());
        dto.setUrgent(post.isUrgent());
        dto.setCreatedAt(post.getCreatedAt());

        if (post.getAuthor() != null) {
            dto.setAuthorFullName(post.getAuthor().getFirstname());
        }

        if (post.getVisibleDepartments() != null) {
            dto.setVisibleDepartments(
                    post.getVisibleDepartments().stream()
                            .map(Department::getDepName)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }
}