package com.example.news.controller;

import com.example.news.dto.UserDTO;
import com.example.news.entity.User;
import com.example.news.exception.UserNotFoundException;
import com.example.news.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@Validated @RequestBody UserDTO userDTO) {
        logger.info("Request received to create a new user with username '{}'", userDTO.getUsername());
        try {
            User createdUser = userService.createUser(userDTO);
            logger.info("User created successfully with id {}", createdUser.getId());
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating user with username '{}'", userDTO.getUsername(), e);
            throw e;
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Validated @RequestBody UserDTO userDTO) {
        logger.info("Request received to update user with id {}", id);
        try {
            User updatedUser = userService.updateUser(id, userDTO);
            logger.info("User updated successfully with id {}", updatedUser.getId());
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error("User not found with id {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error updating user with id {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Optional<User>> deleteUser(@PathVariable Long id) {
        logger.info("Request received to delete user with id {}", id);
        try {
            Optional<User> deletedUser = userService.deleteUser(id);
            if (deletedUser.isPresent()) {
                logger.info("User with id {} deleted successfully", id);
                return new ResponseEntity<>(deletedUser, HttpStatus.OK);
            } else {
                logger.warn("Attempted to delete user with id {} that does not exist", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error deleting user with id {}", id, e);
            throw e;
        }
    }

    @GetMapping("/fetch/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("Request received to fetch user with id {}", id);
        try {
            User user = userService.getUserById(id);
            logger.info("User with id {} fetched successfully", id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error("User not found with id {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error fetching user with id {}", id, e);
            throw e;
        }
    }

    @GetMapping("/fetchAll")
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Request received to fetch all users");
        try {
            List<User> users = userService.getAllUsers();
            logger.info("Fetched {} users", users.size());
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching all users", e);
            throw e;
        }
    }

    @GetMapping("/page")
    public ResponseEntity<Page<User>> getUsers(Pageable pageable) {
        logger.info("Request received to fetch users with pagination and sorting");
        try {
            Page<User> users = userService.getUsers(pageable);
            logger.info("Fetched {} users (page {} of {})",
                    users.getNumberOfElements(),
                    users.getNumber() + 1,
                    users.getTotalPages());
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching users with pagination and sorting", e);
            throw e;
        }
    }
}
