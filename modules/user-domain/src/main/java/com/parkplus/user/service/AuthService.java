package com.parkplus.user.service;

import com.parkplus.common.error.ApiException;
import com.parkplus.common.error.ErrorCodes;
import com.parkplus.user.entities.Profile;
import com.parkplus.user.entities.User;
import com.parkplus.user.repository.ProfileRepository;
import com.parkplus.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    @Transactional
    public UUID register(String email, String username, String rawPassword, String phone, User.Role role) {

        userRepository.findByEmailIgnoreCase(email).ifPresent(u -> {
            throw new ApiException(ErrorCodes.CONFLICT, "Email already exists", 409);
        });

        userRepository.findByUsernameIgnoreCase(username).ifPresent(u -> {
            throw new ApiException(ErrorCodes.CONFLICT, "Username already exists", 409);
        });

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername(username);
        user.setPhoneNumber(phone);
        user.setRole(role);
        user.setPasswordHash(bCryptPasswordEncoder.encode(rawPassword));
        userRepository.save(user);

        Profile profile = new Profile();
        profile.setUserId(user.getId());
        profileRepository.save(profile);

        return user.getId();
    }

    public User authenticate(String emailOrUsername, String rawPassword) {
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(emailOrUsername);

        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByUsernameIgnoreCase(emailOrUsername);
        }

        User user = userOptional.orElseThrow(() -> new ApiException(ErrorCodes.AUTH_FAILED, "Invalid credentials", 401));

        if (!bCryptPasswordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new ApiException(ErrorCodes.AUTH_FAILED, "Invalid credentials", 401);
        }

        if (user.getStatus() != User.Status.ACTIVE) {
            throw new ApiException(ErrorCodes.AUTH_FAILED, "Account disabled", 401);
        }

        return user;
    }
}
