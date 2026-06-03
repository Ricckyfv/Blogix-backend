package com.ricardofernandezv.blog.services.impls;

import com.ricardofernandezv.blog.domain.dtos.AuthorDto;
import com.ricardofernandezv.blog.domain.entities.User;
import com.ricardofernandezv.blog.mappers.UserMapper;
import com.ricardofernandezv.blog.repositories.UserRepository;
import com.ricardofernandezv.blog.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public User getUserById(UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public AuthorDto getCurrentUserProfile(UUID userId) {
        User user = getUserById(userId);
        return userMapper.toAuthorDto(user);
    }

    @Override
    @Transactional
    public AuthorDto updateProfile(UUID userId, AuthorDto updateDto) {
        User user = getUserById(userId);
        user.setName(updateDto.getName());
        user.setDescription(updateDto.getDescription());

        if (updateDto.getProfileImage() != null && !updateDto.getProfileImage().isEmpty()) {
            user.setProfileImage(updateDto.getProfileImage());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toAuthorDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteAccount(UUID userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

}