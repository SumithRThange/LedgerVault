package com.timeboundwallet.controller;

import com.timeboundwallet.dto.UserCreateRequest;
import com.timeboundwallet.dto.UserResponse;
import com.timeboundwallet.dto.UserUpdateRequest;
import com.timeboundwallet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponse> getUserByEmailAndName(@RequestParam String email,
                                                               @RequestParam String name) {
        return ResponseEntity.ok(userService.getUserByEmailAndName(email, name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@RequestHeader("X-USER-ID") Long loggedInUserId,
                                                   @PathVariable Long id,
                                                   @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(loggedInUserId, id, request));
    }
}
