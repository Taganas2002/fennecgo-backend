package com.fennec.fennecgo.services.Interface;

import java.util.List;

import com.fennec.fennecgo.dto.request.UserRequest;
import com.fennec.fennecgo.dto.response.UserResponse;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);
}
