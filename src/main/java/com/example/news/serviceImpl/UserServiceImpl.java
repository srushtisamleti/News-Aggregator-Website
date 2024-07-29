package com.example.news.serviceImpl;

import com.example.news.dto.UserDTO;
import com.example.news.entity.User;
import com.example.news.exception.UserNotFoundException;
import com.example.news.repository.UserRepository;
import com.example.news.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();  // Use BCryptPasswordEncoder for encoding passwords
    }

    @Override
    @Transactional
    public User createUser(UserDTO userDTO) {
        logger.info("Creating a new user with username '{}'", userDTO.getUsername());
        try {
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Encrypt password
            User savedUser = userRepository.save(user);
            logger.info("User created successfully with id {}", savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            logger.error("Error creating user with username '{}'", userDTO.getUsername(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void registerUser(UserDTO registerRequest) {
        logger.info("Registering new user with username '{}'", registerRequest.getUsername());
        try {
            if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Username '" + registerRequest.getUsername() + "' is already taken");
            }
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Encrypt password
            userRepository.save(user);
            logger.info("User registered successfully with username '{}'", registerRequest.getUsername());
        } catch (IllegalArgumentException e) {
            logger.error("Registration failed for user '{}'", registerRequest.getUsername(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error registering user '{}'", registerRequest.getUsername(), e);
            throw e;
        }
    }

    @Override
    public void authenticateUser(UserDTO loginRequest) throws UserNotFoundException {
        logger.info("Authenticating user '{}'", loginRequest.getUsername());
        try {
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new UserNotFoundException("Invalid credentials");
            }
            logger.info("User '{}' authenticated successfully", loginRequest.getUsername());
        } catch (UserNotFoundException e) {
            logger.error("Authentication failed for user '{}'", loginRequest.getUsername(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error authenticating user '{}'", loginRequest.getUsername(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserDTO userDTO) {
        logger.info("Updating user with id {}", id);
        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("No User found with id " + id));
            BeanUtils.copyProperties(userDTO, existingUser, "id", "password");
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Update password if provided
            }
            User updatedUser = userRepository.save(existingUser);
            logger.info("User updated successfully with id {}", id);
            return updatedUser;
        } catch (UserNotFoundException e) {
            logger.error("User not found with id {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating user with id {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Optional<User> deleteUser(Long id) {
        logger.info("Attempting to delete user with id {}", id);
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                userRepository.deleteById(id);
                logger.info("User with id {} deleted successfully", id);
                return userOptional;
            } else {
                logger.warn("Attempted to delete user with id {} that does not exist", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error deleting user with id {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        logger.info("Fetching user with id {}", id);
        try {
            return userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("No User found with id " + id));
        } catch (UserNotFoundException e) {
            logger.error("User not found with id {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching user with id {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching all users", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getUsers(Pageable pageable) {
        logger.info("Fetching users with pagination and sorting");
        try {
            return userRepository.findAll(pageable);
        } catch (Exception e) {
            logger.error("Error fetching users with pagination and sorting", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        String currentUsername = getCurrentUsername();
        logger.info("Fetching current user with username '{}'", currentUsername);
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("No User found with username " + currentUsername));
        return UserDTO.convertToDTO(user);
    }

    @Override
    @Transactional
    public void updateUserSettings(UserDTO userDTO) {
        logger.info("Updating settings for user '{}'", userDTO.getUsername());
        try {
            User existingUser = userRepository.findByUsername(userDTO.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("No User found with username " + userDTO.getUsername()));
            BeanUtils.copyProperties(userDTO, existingUser, "id", "password", "username");
            userRepository.save(existingUser);
            logger.info("Settings updated successfully for user '{}'", userDTO.getUsername());
        } catch (UserNotFoundException e) {
            logger.error("User not found with username '{}'", userDTO.getUsername(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating settings for user '{}'", userDTO.getUsername(), e);
            throw e;
        }
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
