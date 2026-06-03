package com.ricardofernandezv.blog.services.impls;

import com.ricardofernandezv.blog.domain.entities.Comment;
import com.ricardofernandezv.blog.domain.entities.Post;
import com.ricardofernandezv.blog.domain.entities.User;
import com.ricardofernandezv.blog.repositories.CommentRepository;
import com.ricardofernandezv.blog.repositories.PostRepository;
import com.ricardofernandezv.blog.repositories.UserRepository;
import com.ricardofernandezv.blog.services.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getComments(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post does not exist with ID " + postId);
        }
        return commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId);
    }

    @Override
    @Transactional
    public Comment createComment(UUID postId, UUID userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post does not exist with ID " + postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist with ID " + userId));

        Comment comment = Comment.builder()
                .content(content)
                .post(post)
                .author(user)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment does not exist with ID " + commentId));

        boolean isCommentAuthor = comment.getAuthor().getId().equals(userId);
        boolean isPostCreator = comment.getPost().getAuthor().getId().equals(userId);

        if (!isCommentAuthor && !isPostCreator) {
            throw new AccessDeniedException("No estás autorizado para eliminar este comentario.");
        }

        commentRepository.delete(comment);
    }
}
