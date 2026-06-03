package com.ricardofernandezv.blog.services;

import com.ricardofernandezv.blog.domain.entities.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    List<Comment> getComments(UUID postId);
    Comment createComment(UUID postId, UUID userId, String content);
    void deleteComment(UUID commentId, UUID userId);
}
