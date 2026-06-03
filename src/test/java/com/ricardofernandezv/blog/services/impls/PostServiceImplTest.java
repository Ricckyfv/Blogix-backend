package com.ricardofernandezv.blog.services.impls;

import com.ricardofernandezv.blog.domain.CreatePostRequest;
import com.ricardofernandezv.blog.domain.PostStatus;
import com.ricardofernandezv.blog.domain.UpdatePostRequest;
import com.ricardofernandezv.blog.domain.entities.Category;
import com.ricardofernandezv.blog.domain.entities.Post;
import com.ricardofernandezv.blog.domain.entities.Tag;
import com.ricardofernandezv.blog.domain.entities.User;
import com.ricardofernandezv.blog.repositories.PostRepository;
import com.ricardofernandezv.blog.services.CategoryService;
import com.ricardofernandezv.blog.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private PostServiceImpl postService;

    private UUID postId;
    private UUID userId;
    private User author;
    private Post post;
    private Category category;
    private Tag tag;

    @BeforeEach
    void setUp() {
        postId = UUID.randomUUID();
        userId = UUID.randomUUID();

        author = User.builder()
                .id(userId)
                .name("Ricardo")
                .email("ricardo@gmail.com")
                .build();

        category = Category.builder()
                .id(UUID.randomUUID())
                .name("Tecnología")
                .build();

        tag = Tag.builder()
                .id(UUID.randomUUID())
                .name("Spring")
                .build();

        post = Post.builder()
                .id(postId)
                .title("Mi primer artículo")
                .content("Este es el contenido de prueba para validar lectura.")
                .status(PostStatus.PUBLISHED)
                .readingTime(2)
                .author(author)
                .category(category)
                .tags(new HashSet<>(Collections.singletonList(tag)))
                .postImage("base64-image-string")
                .build();
    }

    @Test
    void getPost_PostExists_ReturnsPost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.getPost(postId);

        assertNotNull(result);
        assertEquals(postId, result.getId());
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void getPost_PostDoesNotExist_ThrowsEntityNotFoundException() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getPost(postId));
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void createPost_ValidRequest_CreatesAndReturnsPost() {
        CreatePostRequest request = CreatePostRequest.builder()
                .title("Nuevo Post")
                .content("Contenido del post para probar la creación del sistema.")
                .categoryId(category.getId())
                .tagIds(new HashSet<>(Collections.singletonList(tag.getId())))
                .status(PostStatus.PUBLISHED)
                .postImage("new-base64-string")
                .build();

        when(categoryService.getCategoryById(category.getId())).thenReturn(category);
        when(tagService.getTagByIds(any())).thenReturn(Collections.singletonList(tag));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.createPost(author, request);

        assertNotNull(result);
        assertEquals("Nuevo Post", result.getTitle());
        assertEquals("new-base64-string", result.getPostImage());
        assertEquals(author, result.getAuthor());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void updatePost_AuthorizedUser_UpdatesPostSuccessfully() {
        UpdatePostRequest request = UpdatePostRequest.builder()
                .id(postId)
                .title("Título Actualizado")
                .content("Contenido actualizado para el post de pruebas con más de diez palabras.")
                .categoryId(category.getId())
                .tagIds(new HashSet<>(Collections.singletonList(tag.getId())))
                .status(PostStatus.PUBLISHED)
                .postImage("updated-base64-string")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.updatePost(postId, request, userId);

        assertNotNull(result);
        assertEquals("Título Actualizado", result.getTitle());
        assertEquals("updated-base64-string", result.getPostImage());
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void updatePost_UnauthorizedUser_ThrowsAccessDeniedException() {
        UpdatePostRequest request = UpdatePostRequest.builder()
                .id(postId)
                .title("Título Actualizado")
                .content("Contenido actualizado.")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        UUID strangerId = UUID.randomUUID();

        assertThrows(AccessDeniedException.class, () -> postService.updatePost(postId, request, strangerId));
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void deletePost_AuthorizedUser_DeletesSuccessfully() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        doNothing().when(postRepository).delete(post);

        assertDoesNotThrow(() -> postService.deletePost(postId, userId));

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void deletePost_UnauthorizedUser_ThrowsAccessDeniedException() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        UUID strangerId = UUID.randomUUID();

        assertThrows(AccessDeniedException.class, () -> postService.deletePost(postId, strangerId));
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    void getMyPosts_ValidUser_ReturnsPosts() {
        when(postRepository.findAllByAuthorOrderByCreatedAtDesc(author)).thenReturn(Collections.singletonList(post));

        List<Post> result = postService.getMyPosts(author);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(postId, result.get(0).getId());
        verify(postRepository, times(1)).findAllByAuthorOrderByCreatedAtDesc(author);
    }
}
