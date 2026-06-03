package com.ricardofernandezv.blog.services.impls;

import com.ricardofernandezv.blog.domain.dtos.AuthorDto;
import com.ricardofernandezv.blog.domain.entities.User;
import com.ricardofernandezv.blog.mappers.UserMapper;
import com.ricardofernandezv.blog.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User user;
    private AuthorDto authorDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .name("Ricardo Fernández")
                .email("ricardo@gmail.com")
                .description("Ingeniero de Software")
                .profileImage("avatar-preset")
                .build();

        authorDto = AuthorDto.builder()
                .id(userId)
                .name("Ricardo Fernández")
                .description("Ingeniero de Software")
                .profileImage("avatar-preset")
                .email("ricardo@gmail.com")
                .build();
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Ricardo Fernández", result.getName());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsEntityNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getCurrentUserProfile_UserExists_ReturnsAuthorDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toAuthorDto(user)).thenReturn(authorDto);

        AuthorDto result = userService.getCurrentUserProfile(userId);

        assertNotNull(result);
        assertEquals("Ricardo Fernández", result.getName());
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toAuthorDto(user);
    }

    @Test
    void updateProfile_UserExists_UpdatesAndReturnsAuthorDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toAuthorDto(user)).thenReturn(authorDto);

        AuthorDto updateDto = AuthorDto.builder()
                .name("Ricardo Fernandez V")
                .description("Senior Angular & Spring Boot Developer")
                .profileImage("new-avatar-preset")
                .build();

        AuthorDto result = userService.updateProfile(userId, updateDto);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteAccount_UserExists_DeletesSuccessfully() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> userService.deleteAccount(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
    }
}
