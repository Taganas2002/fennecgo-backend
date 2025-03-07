package com.fennec.fennecgo.dto.response;

import lombok.Data;

@Data
public class FavoriteResponse {
    private Long id;
    private Long favoriteId;
    private String favoriteUsername;
    private String favoriteEmail;
}
