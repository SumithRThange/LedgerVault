package com.timeboundwallet.service;

import com.timeboundwallet.dto.UserCreateRequest;
import com.timeboundwallet.dto.UserResponse;
import com.timeboundwallet.dto.UserUpdateRequest;
import com.timeboundwallet.entity.User;
import com.timeboundwallet.exception.AccessDeniedException;
import com.timeboundwallet.exception.BusinessException;
import com.timeboundwallet.exception.ResourceNotFoundException;
import com.timeboundwallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail().trim())) {
            throw new BusinessException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim())
                .build();

        User saved = userRepository.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .build();
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        return toUserResponse(user);
    }

    public UserResponse getUserByEmailAndName(String email, String name) {
        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found for provided name/email"));

        String requestedName = normalize(name);
        String storedName = normalize(user.getName());
        if (!storedName.equals(requestedName)) {
            throw new ResourceNotFoundException("User not found for provided name/email");
        }

        return toUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();
    }

    public UserResponse updateUser(Long loggedInUserId, Long userId, UserUpdateRequest request) {
        if (!loggedInUserId.equals(userId)) {
            throw new AccessDeniedException("You can edit only your own credentials");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        String newEmail = request.getEmail() == null ? "" : request.getEmail().trim();
        String newName = request.getName() == null ? "" : request.getName().trim();

        if (newEmail.isEmpty() && newName.isEmpty()) {
            throw new BusinessException("Provide name or email to update");
        }

        if (!newEmail.isEmpty()) {
            userRepository.findByEmailIgnoreCase(newEmail).ifPresent(existing -> {
                if (!existing.getId().equals(userId)) {
                    throw new BusinessException("Email already exists");
                }
            });
            user.setEmail(newEmail);
        }

        if (!newName.isEmpty()) {
            user.setName(newName);
        }

        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
