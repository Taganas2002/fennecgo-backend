package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.request.FavoriteRequest;
import com.fennec.fennecgo.dto.response.FavoriteResponse;
import com.fennec.fennecgo.models.Favorites;
import com.fennec.fennecgo.models.User;
import com.fennec.fennecgo.repository.FavoritesRepository;
import com.fennec.fennecgo.repository.UserRepository;
import com.fennec.fennecgo.services.Interface.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritesServiceImpl implements FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final UserRepository userRepository;

    @Override
    public FavoriteResponse addFavorite(Long userId, FavoriteRequest request) {
        // Load the authenticated user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Load the user to be favorited
        User favoriteUser = userRepository.findById(request.getFavoriteId())
            .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getFavoriteId()));
        
        // Create a new favorite record
        Favorites fav = new Favorites();
        fav.setUser(user);
        fav.setFavorite(favoriteUser);
        Favorites savedFav = favoritesRepository.save(fav);

        FavoriteResponse response = new FavoriteResponse();
        response.setId(savedFav.getId());
        response.setFavoriteId(favoriteUser.getId());
        response.setFavoriteUsername(favoriteUser.getUsername());
        response.setFavoriteEmail(favoriteUser.getEmail());
        return response;
    }

    @Override
    public void removeFavorite(Long userId, Long favoriteId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        User favoriteUser = userRepository.findById(favoriteId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + favoriteId));
        favoritesRepository.deleteByUserAndFavorite(user, favoriteUser);
    }

    @Override
    public List<FavoriteResponse> getFavorites(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        List<Favorites> favList = favoritesRepository.findByUser(user);
        return favList.stream().map(fav -> {
            FavoriteResponse resp = new FavoriteResponse();
            resp.setId(fav.getId());
            resp.setFavoriteId(fav.getFavorite().getId());
            resp.setFavoriteUsername(fav.getFavorite().getUsername());
            resp.setFavoriteEmail(fav.getFavorite().getEmail());
            return resp;
        }).collect(Collectors.toList());
    }
}
