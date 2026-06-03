package com.ricardofernandezv.blog.services;

import com.ricardofernandezv.blog.domain.CreatePostRequest;
import com.ricardofernandezv.blog.domain.UpdatePostRequest;
import com.ricardofernandezv.blog.domain.entities.Post;
import com.ricardofernandezv.blog.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface PostService {
    Post getPost(UUID id);
    List<Post> getAllPosts(UUID categoryId, UUID tagId);
    List<Post> getDraftPosts(User user);
    List<Post> getMyPosts(User user);
    Post createPost(User user, CreatePostRequest createPostRequest);
    Post updatePost(UUID id, UpdatePostRequest updatePostRequest, UUID authenticatedUserId);
    void deletePost(UUID id, UUID authenticatedUserId);
}
