package com.parkplus.user.controller;

import com.parkplus.user.entities.Address;
import com.parkplus.user.entities.Profile;
import com.parkplus.user.entities.User;
import com.parkplus.user.repository.AddressRepository;
import com.parkplus.user.repository.ProfileRepository;
import com.parkplus.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class MeController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private AddressRepository addressRepository;

    record UpdateProfileReq(String firstName, String lastName, String govtIdType, String govtIdNumber, String avatarUrl,
                            java.sql.Date dob) {
    }

    @GetMapping("/users/me")
    public Map<String, Object> me(Authentication authentication) {
        UUID uid = UUID.fromString(authentication.getName());
        User user = userRepository.findById(uid).orElseThrow();
        Profile profile = profileRepository.findById(uid).orElse(null);
        List<Address> addressList = addressRepository.findByUserId(uid);

        return Map.of("user", user, "profile", profile, "addresses", addressList);
    }

    @PutMapping("/profiles/me")
    public Map<String, Object> updateProfile(Authentication auth, @RequestBody UpdateProfileReq updateProfileReq) {
        UUID uid = UUID.fromString(auth.getName());

        Profile profile = profileRepository.findById(uid).orElseGet(() -> {
            Profile newProfile = new Profile();
            newProfile.setUserId(uid);
            return newProfile;
        });
        profile.setFirstName(updateProfileReq.firstName());
        profile.setLastName(updateProfileReq.lastName());
        profile.setGovtIdType(updateProfileReq.govtIdType());
        profile.setGovtIdNumber(updateProfileReq.govtIdNumber());
        profile.setAvatarUrl(updateProfileReq.avatarUrl());
        profile.setDob(updateProfileReq.dob());
        profileRepository.save(profile);

        return Map.of("profile", profile);
    }
}
