package com.ricardofernandezv.blog.services;

import com.ricardofernandezv.blog.domain.dtos.AuthorDto;
import com.ricardofernandezv.blog.domain.entities.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
    AuthorDto getCurrentUserProfile(UUID userId);
    AuthorDto updateProfile(UUID userId, AuthorDto updateDto);
    void deleteAccount(UUID userId);
}
