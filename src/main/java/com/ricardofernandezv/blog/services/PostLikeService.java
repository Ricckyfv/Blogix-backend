package com.ricardofernandezv.blog.services;

import java.util.UUID;

public interface PostLikeService {
    boolean toggleLike(UUID postId, UUID userId);
    long getLikesCount(UUID postId);
    boolean isLikedByUser(UUID postId, UUID userId);
}
