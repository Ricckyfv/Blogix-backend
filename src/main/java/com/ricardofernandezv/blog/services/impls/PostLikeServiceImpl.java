package com.ricardofernandezv.blog.services.impls;

import com.ricardofernandezv.blog.domain.entities.Post;
import com.ricardofernandezv.blog.domain.entities.PostLike;
import com.ricardofernandezv.blog.domain.entities.User;
import com.ricardofernandezv.blog.repositories.PostLikeRepository;
import com.ricardofernandezv.blog.repositories.PostRepository;
import com.ricardofernandezv.blog.repositories.UserRepository;
import com.ricardofernandezv.blog.services.PostLikeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public boolean toggleLike(UUID postId, UUID userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post does not exist with ID " + postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist with ID " + userId));

        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            return false; // unliked
        } else {
            PostLike newLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeRepository.save(newLike);
            return true; // liked
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikesCount(UUID postId) {
        return postLikeRepository.countByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLikedByUser(UUID postId, UUID userId) {
        if (userId == null) return false;
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }
}
