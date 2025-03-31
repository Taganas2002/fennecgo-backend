package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.response.UserResponse;
import com.fennec.fennecgo.dto.response.UserSearchResponse;
import com.fennec.fennecgo.dto.request.UserRequest;
import com.fennec.fennecgo.dto.request.UserSearchRequest;
import com.fennec.fennecgo.services.Interface.FileStorageService;
import com.fennec.fennecgo.services.Interface.UserSearchService;
import com.fennec.fennecgo.services.Interface.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserSearchService userSearchService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

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

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    // Combined update endpoint using multipart/form-data (for user fields and file upload)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("gender") String gender,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhotoFile) {

        // If file is provided, store it and retrieve the URL/path
        String profilePhotoUrl = null;
        if (profilePhotoFile != null && !profilePhotoFile.isEmpty()) {
            profilePhotoUrl = fileStorageService.storeFile(profilePhotoFile);
        }

        // Build the UserRequest DTO using provided parameters
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(username);
        userRequest.setEmail(email);
        userRequest.setGender(gender);
        userRequest.setProfilePhoto(profilePhotoUrl);

        UserResponse response = userService.updateUser(id, userRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
