package com.fennec.fennecgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fennec.fennecgo.dto.response.UserSearchResponse;
import com.fennec.fennecgo.services.Interface.UserSearchService;
import com.fennec.fennecgo.services.Interface.UserService;
import com.fennec.fennecgo.dto.request.UserSearchRequest;
import com.fennec.fennecgo.dto.request.UserRequest;
import com.fennec.fennecgo.dto.response.UserResponse;


import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {


    private final UserSearchService userSearchService;
    private final UserService userService;

    
    @PostMapping("/search")
    public ResponseEntity<List<UserSearchResponse>> searchUsers(@RequestBody UserSearchRequest request) {
        List<UserSearchResponse> results = userSearchService.searchUsers(request);
        return ResponseEntity.ok(results);
    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> responses = userService.getAllUsers();
        return ResponseEntity.ok(responses);
    }

    // GET user by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }


    // PUT update user
    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        UserResponse response = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(response);
    }

    // DELETE user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
