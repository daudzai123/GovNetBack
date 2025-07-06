package com.govnet.govnet.service;

import com.govnet.govnet.dto.CommentRequestDTO;
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

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MyUserRepository userRepository;

    public Comment addComment(CommentRequestDTO dto, String username) {
        MyUser user = userRepository.findByEmail(username) // instead of findByUsername
                .orElseThrow(() -> new RuntimeException("User not found"));


        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setPost(post);
        comment.setAuthor(user);

        return commentRepository.save(comment);
    }


    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    public Comment updateComment(Long commentId, CommentRequestDTO dto, String username) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to update this comment.");
        }

        comment.setContent(dto.getContent());
        // Optionally update post if needed, but usually comments belong to a fixed post.
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, String username) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to delete this comment.");
        }

        commentRepository.delete(comment);
    }

    public List<Comment> getCommentsForPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }
}

