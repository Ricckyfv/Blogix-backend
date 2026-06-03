package com.ricardofernandezv.blog.controllers;

import com.ricardofernandezv.blog.domain.dtos.AuthorDto;
import com.ricardofernandezv.blog.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(path = "/me")
    public ResponseEntity<AuthorDto> getMyProfile(@RequestAttribute UUID userId) {
        AuthorDto profile = userService.getCurrentUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping(path = "/me")
    public ResponseEntity<AuthorDto> updateMyProfile(
            @Valid @RequestBody AuthorDto updateDto,
            @RequestAttribute UUID userId) {
        AuthorDto updatedProfile = userService.updateProfile(userId, updateDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping(path = "/me")
    public ResponseEntity<Void> deleteMyAccount(@RequestAttribute UUID userId) {
        userService.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }
}
