package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.FavoriteRequest;
import com.fennec.fennecgo.dto.response.FavoriteResponse;
import com.fennec.fennecgo.security.services.UserDetailsImpl;
import com.fennec.fennecgo.services.Interface.FavoritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoritesController {

  
    private final FavoritesService favoritesService;

    // Helper method to get the current authenticated user's ID
    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    // Endpoint to add a favorite user
    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(@RequestBody FavoriteRequest favoriteRequest) {
        Long userId = getCurrentUserId();
        FavoriteResponse response = favoritesService.addFavorite(userId, favoriteRequest);
        return ResponseEntity.ok(response);
    }

    // Endpoint to retrieve all favorites for the current user
    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getFavorites() {
        Long userId = getCurrentUserId();
        List<FavoriteResponse> responses = favoritesService.getFavorites(userId);
        return ResponseEntity.ok(responses);
    }

    // Endpoint to remove a favorite user
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long favoriteId) {
        Long userId = getCurrentUserId();
        favoritesService.removeFavorite(userId, favoriteId);
        return ResponseEntity.noContent().build();
    }
}
