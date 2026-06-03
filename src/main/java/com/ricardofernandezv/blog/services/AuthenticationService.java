package com.ricardofernandezv.blog.services;

import com.ricardofernandezv.blog.domain.dtos.RegisterRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    UserDetails authenticate(String email, String password);
    String generateToken(UserDetails userDetails);
    UserDetails validateToken(String token);
    UserDetails register(RegisterRequest registerRequest);
    UserDetails authenticateGoogle(String googleToken);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
}
