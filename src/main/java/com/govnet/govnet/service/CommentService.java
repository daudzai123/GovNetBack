package com.govnet.govnet.service;

import com.govnet.govnet.dto.CommentRequestDTO;
import com.govnet.govnet.dto.CommentResponseDTO;
import com.govnet.govnet.entity.Comment;
import com.govnet.govnet.entity.MyUser;
import com.govnet.govnet.entity.Post;
import com.govnet.govnet.repo.CommentRepository;
import com.govnet.govnet.repo.MyUserRepository;
import com.govnet.govnet.repo.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MyUserRepository userRepository;

    public CommentResponseDTO addComment(CommentRequestDTO dto, String username) {
        MyUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setPost(post);
        comment.setAuthor(user);

        Comment saved = commentRepository.save(comment);
        return mapToDTO(saved);
    }

    public CommentResponseDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return mapToDTO(comment);
    }

    public CommentResponseDTO updateComment(Long commentId, CommentRequestDTO dto, String username) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        MyUser currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to update this comment.");
        }

        comment.setContent(dto.getContent());
        Comment updated = commentRepository.save(comment);
        return mapToDTO(updated);
    }


    public void deleteComment(Long commentId, String username) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        MyUser currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to update this comment.");
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponseDTO> getCommentsForPost(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ==========================
    // Mapper Method
    // ==========================
    private CommentResponseDTO mapToDTO(Comment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());

        if (comment.getAuthor() != null) {
            dto.setAuthorId(comment.getAuthor().getId());
            dto.setAuthorName(comment.getAuthor().getUsername());
            dto.setAuthorProfileImage(comment.getAuthor().getProfileImage());
        }

        if (comment.getPost() != null) {
            dto.setPostId(comment.getPost().getId());
        }

        return dto;
    }
}
