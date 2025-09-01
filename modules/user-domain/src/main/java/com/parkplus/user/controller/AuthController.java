package com.parkplus.user.controller;

import com.parkplus.security.service.JwtService;
import com.parkplus.user.entities.User;
import com.parkplus.user.service.AuthService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

record RegisterReq(@Email String email, @NotBlank String username, @Size(min = 6) String password, String phone ) {
}

record LoginReq(@NotBlank String emailOrUsername, @NotBlank String password) {
}

record TokenRes(String accessToken, long expiresInSeconds, UUID userId, String role) {
}

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterReq registerReq) {
        UUID id = authService.register(registerReq.email(), registerReq.username(), registerReq.password(),
                registerReq.phone(), User.Role.CUSTOMER);

        return Map.of("userId", id);
    }

    @PostMapping("/admin/register")
    public Map<String, Object> adminRegister(@RequestBody RegisterReq registerReq) {
        UUID id = authService.register(registerReq.email(), registerReq.username(), registerReq.password(),
                registerReq.phone(), User.Role.ADMIN);

        return Map.of("userId", id);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenRes> login(@RequestBody LoginReq loginReq) {
        User user = authService.authenticate(loginReq.emailOrUsername(), loginReq.password());

        String token = jwtService.issue(user.getId().toString(),
                Map.of("role", user.getRole().name(), "email", user.getEmail(), "name", user.getUsername())
        );

        return ResponseEntity.ok(new TokenRes(token, 3600, user.getId(), user.getRole().name()));
    }
}
