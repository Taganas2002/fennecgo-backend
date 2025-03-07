package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.response.UserResponse;
import com.fennec.fennecgo.dto.request.UserRequest;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.dto.mapper.UserMapper;
import com.fennec.fennecgo.models.User;
import com.fennec.fennecgo.repository.UserRepository;
import com.fennec.fennecgo.services.Interface.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUsers() {
        LOGGER.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        LOGGER.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.toUserResponse(user);
    }



    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        LOGGER.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userMapper.updateUserFromRequest(userRequest, user);
        User updatedUser = userRepository.save(user);
        LOGGER.info("User with id {} updated successfully", id);
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        LOGGER.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
        LOGGER.info("User with id {} deleted successfully", id);
    }
}
