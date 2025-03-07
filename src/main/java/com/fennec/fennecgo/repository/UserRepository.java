package com.fennec.fennecgo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fennec.fennecgo.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByPhone(String PhoneNumber);
  
  Boolean existsByPhone(String username);

  Boolean existsByEmail(String email);
}
