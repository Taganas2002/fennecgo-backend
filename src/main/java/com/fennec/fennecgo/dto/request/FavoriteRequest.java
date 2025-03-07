package com.fennec.fennecgo.dto.request;

import lombok.Data;

@Data
public class FavoriteRequest {
    // The ID of the user to be added as a favorite.
    private Long favoriteId;
}
