package com.ricardofernandezv.blog.controllers;

import com.ricardofernandezv.blog.domain.CreatePostRequest;
import com.ricardofernandezv.blog.domain.UpdatePostRequest;
import com.ricardofernandezv.blog.domain.dtos.CommentDto;
import com.ricardofernandezv.blog.domain.dtos.CreateCommentRequest;
import com.ricardofernandezv.blog.domain.dtos.CreatePostRequestDto;
import com.ricardofernandezv.blog.domain.dtos.PostDto;
import com.ricardofernandezv.blog.domain.dtos.UpdatePostRequestDto;
import com.ricardofernandezv.blog.domain.entities.Comment;
import com.ricardofernandezv.blog.domain.entities.Post;
import com.ricardofernandezv.blog.domain.entities.User;
import com.ricardofernandezv.blog.mappers.CommentMapper;
import com.ricardofernandezv.blog.mappers.PostMapper;
import com.ricardofernandezv.blog.services.CommentService;
import com.ricardofernandezv.blog.services.PostLikeService;
import com.ricardofernandezv.blog.services.PostService;
import com.ricardofernandezv.blog.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    private PostDto mapToDtoWithLikes(Post post, UUID userId) {
        PostDto dto = postMapper.toDto(post);
        dto.setLikesCount(postLikeService.getLikesCount(post.getId()));
        dto.setLikedByMe(postLikeService.isLikedByUser(post.getId(), userId));
        return dto;
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId,
            @RequestAttribute(required = false) UUID userId) {
        List<Post> posts = postService.getAllPosts(categoryId, tagId);
        List<PostDto> postDtos = posts.stream().map(post -> mapToDtoWithLikes(post, userId)).toList();
        return ResponseEntity.ok(postDtos);
    }

    @GetMapping(path = "/drafts")
    public ResponseEntity<List<PostDto>> getDrafts(@RequestAttribute UUID userId) {
        User loggedInUser = userService.getUserById(userId);
        List<Post> draftPosts = postService.getDraftPosts(loggedInUser);
        List<PostDto> postDtos = draftPosts.stream().map(post -> mapToDtoWithLikes(post, userId)).toList();
        return ResponseEntity.ok(postDtos);
    }

    @GetMapping(path = "/my-posts")
    public ResponseEntity<List<PostDto>> getMyPosts(@RequestAttribute UUID userId) {
        User loggedInUser = userService.getUserById(userId);
        List<Post> myPosts = postService.getMyPosts(loggedInUser);
        List<PostDto> postDtos = myPosts.stream().map(post -> mapToDtoWithLikes(post, userId)).toList();
        return ResponseEntity.ok(postDtos);
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @Valid @RequestBody CreatePostRequestDto createPostRequestDto,
            @RequestAttribute UUID userId) {
        User loggedInUser = userService.getUserById(userId);
        CreatePostRequest createPostRequest = postMapper.toCreatePostRequest(createPostRequestDto);
        Post createdPost = postService.createPost(loggedInUser, createPostRequest);
        PostDto createdPostDto = mapToDtoWithLikes(createdPost, userId);
        return new ResponseEntity<>(createdPostDto, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePostRequestDto updatePostRequestDto,
            @RequestAttribute UUID userId) {
        UpdatePostRequest updatePostRequest = postMapper.toUpdatePostRequest(updatePostRequestDto);
        Post updatedPost = postService.updatePost(id, updatePostRequest, userId);
        PostDto updatedPostDto = mapToDtoWithLikes(updatedPost, userId);
        return ResponseEntity.ok(updatedPostDto);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PostDto> getPost(
            @PathVariable UUID id,
            @RequestAttribute(required = false) UUID userId
    ) {
        Post post = postService.getPost(id);
        PostDto postDto = mapToDtoWithLikes(post, userId);
        return ResponseEntity.ok(postDto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID id,
            @RequestAttribute UUID userId) {
        postService.deletePost(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{id}/like")
    public ResponseEntity<PostDto> toggleLike(
            @PathVariable UUID id,
            @RequestAttribute UUID userId) {
        postLikeService.toggleLike(id, userId);
        Post post = postService.getPost(id);
        PostDto postDto = mapToDtoWithLikes(post, userId);
        return ResponseEntity.ok(postDto);
    }

    @GetMapping(path = "/{id}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable UUID id) {
        List<Comment> comments = commentService.getComments(id);
        List<CommentDto> dtos = comments.stream().map(commentMapper::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping(path = "/{id}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCommentRequest request,
            @RequestAttribute UUID userId) {
        Comment created = commentService.createComment(id, userId, request.getContent());
        CommentDto dto = commentMapper.toDto(created);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID id,
            @PathVariable UUID commentId,
            @RequestAttribute UUID userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

}
