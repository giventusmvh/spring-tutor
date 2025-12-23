package com.gvn.springtutor.service;

import com.gvn.springtutor.entity.User;
import com.gvn.springtutor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * User Service dengan Redis Caching.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Create user dan hapus cache.
     */
    @CacheEvict(value = "users", allEntries = true)
    public User createUser(User user) {
        log.info("Creating user: {}", user.getUsername());
        return userRepository.save(user);
    }

    /**
     * Get all users dengan caching.
     */
    @Cacheable(value = "users", key = "'allUsers'")
    public List<User> getAllUsers() {
        log.info("Fetching all users from DATABASE");
        return userRepository.findAll();
    }

    /**
     * Find user by username dengan caching.
     */
    @Cacheable(value = "users", key = "#username")
    public Optional<User> findByUsername(String username) {
        log.info("Fetching user '{}' from DATABASE", username);
        return userRepository.findByUsername(username);
    }

    /**
     * Get user by ID dengan caching.
     */
    @Cacheable(value = "users", key = "'user_' + #id")
    public User getUserById(Long id) {
        log.info("Fetching user with ID {} from DATABASE", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * Update user dan hapus cache.
     */
    @CacheEvict(value = "users", allEntries = true)
    public User updateUser(Long id, User userDetails) {
        log.info("Updating user with ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(userDetails.getPassword());
        }
        if (userDetails.getRoles() != null) {
            existingUser.setRoles(userDetails.getRoles());
        }

        return userRepository.save(existingUser);
    }

    /**
     * Delete user dan hapus cache.
     */
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(existingUser);
    }
}
