package com.fennec.fennecgo.repository;

import com.fennec.fennecgo.models.Favorites;
import com.fennec.fennecgo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
    // Retrieve all favorites for a given user.
    List<Favorites> findByUser(User user);
    
    // Optionally, for deletion: remove a favorite record based on both user and favorite.
    @Modifying
    @Transactional
    void deleteByUserAndFavorite(User user, User favorite);
}
