package com.ricardofernandezv.blog.services.impls;

import com.ricardofernandezv.blog.domain.entities.PasswordResetToken;
import com.ricardofernandezv.blog.domain.entities.User;
import com.ricardofernandezv.blog.repositories.PasswordResetTokenRepository;
import com.ricardofernandezv.blog.repositories.UserRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User user;
    private String email;

    @BeforeEach
    void setUp() {
        email = "user@test.com";
        user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .name("Test User")
                .password("old-encoded-password")
                .build();
    }

    @Test
    void forgotPassword_UserExists_GeneratesTokenAndSendsEmail() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        doNothing().when(passwordResetTokenRepository).deleteByUser(user);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(i -> i.getArgument(0));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> authenticationService.forgotPassword(email));

        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordResetTokenRepository, times(1)).deleteByUser(user);
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void forgotPassword_UserDoesNotExist_ThrowsEntityNotFoundException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authenticationService.forgotPassword(email));

        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordResetTokenRepository, never()).deleteByUser(any());
        verify(passwordResetTokenRepository, never()).save(any());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void resetPassword_ValidToken_ResetsPasswordSuccessfully() {
        String tokenStr = "valid-uuid-token";
        PasswordResetToken token = PasswordResetToken.builder()
                .token(tokenStr)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();

        when(passwordResetTokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPassword")).thenReturn("new-encoded-password");
        when(userRepository.save(user)).thenReturn(user);
        doNothing().when(passwordResetTokenRepository).delete(token);

        assertDoesNotThrow(() -> authenticationService.resetPassword(tokenStr, "newPassword"));

        assertEquals("new-encoded-password", user.getPassword());
        verify(passwordResetTokenRepository, times(1)).findByToken(tokenStr);
        verify(userRepository, times(1)).save(user);
        verify(passwordResetTokenRepository, times(1)).delete(token);
    }

    @Test
    void resetPassword_ExpiredToken_ThrowsIllegalArgumentException() {
        String tokenStr = "expired-uuid-token";
        PasswordResetToken token = PasswordResetToken.builder()
                .token(tokenStr)
                .user(user)
                .expiryDate(LocalDateTime.now().minusMinutes(5)) // already expired
                .build();

        when(passwordResetTokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));
        doNothing().when(passwordResetTokenRepository).delete(token);

        assertThrows(IllegalArgumentException.class, () -> authenticationService.resetPassword(tokenStr, "newPassword"));

        verify(passwordResetTokenRepository, times(1)).findByToken(tokenStr);
        verify(userRepository, never()).save(any());
        verify(passwordResetTokenRepository, times(1)).delete(token);
    }

    @Test
    void resetPassword_InvalidToken_ThrowsEntityNotFoundException() {
        String tokenStr = "invalid-token";
        when(passwordResetTokenRepository.findByToken(tokenStr)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authenticationService.resetPassword(tokenStr, "newPassword"));

        verify(passwordResetTokenRepository, times(1)).findByToken(tokenStr);
        verify(userRepository, never()).save(any());
        verify(passwordResetTokenRepository, never()).delete(any());
    }
}
