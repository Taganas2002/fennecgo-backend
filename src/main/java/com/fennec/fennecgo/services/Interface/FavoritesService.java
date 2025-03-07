package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.request.FavoriteRequest;
import com.fennec.fennecgo.dto.response.FavoriteResponse;
import java.util.List;

public interface FavoritesService {
 FavoriteResponse addFavorite(Long userId, FavoriteRequest request);
 void removeFavorite(Long userId, Long favoriteId);
 List<FavoriteResponse> getFavorites(Long userId);
}

