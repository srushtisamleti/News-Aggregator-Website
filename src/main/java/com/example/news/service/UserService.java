package com.example.news.service;

import com.example.news.dto.UserDTO;
import com.example.news.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(UserDTO userDTO);
    User updateUser(Long id, UserDTO userDTO);
    Optional<User> deleteUser(Long id);
    User getUserById(Long id);
    List<User> getAllUsers();
    Page<User> getUsers(Pageable pageable);

    UserDTO getCurrentUser();

    void updateUserSettings(UserDTO userDTO);

    void authenticateUser(UserDTO loginRequest);

    void registerUser(UserDTO registerRequest);

}
