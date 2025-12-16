package com.anu.aijobmatching.user;

import java.time.LocalDateTime;

import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.anu.aijobmatching.security.JwtService;
import com.anu.aijobmatching.user.dto.UserLoginRequest;
import com.anu.aijobmatching.user.dto.UserLoginResponse;
import com.anu.aijobmatching.user.dto.UserMeResponse;
import com.anu.aijobmatching.user.dto.UserRegisterRequest;
import com.anu.aijobmatching.user.dto.UserRegisterResponse;
import com.anu.aijobmatching.user.exception.InvalidCredentialsException;
import com.anu.aijobmatching.user.exception.UserNotFoundException;

import jakarta.validation.Valid;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserRegisterResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder().name(request.name()).email(request.email())
                .passwordHash(passwordEncoder.encode(request.password())).createdAt(LocalDateTime.now()).role(Role.USER)
                .build();

        User saved = userRepository.save(user);

        return new UserRegisterResponse(saved.getId(), saved.getName(), saved.getEmail());
    }

    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("user not found:" + request.email()));

        if (!validatePassword(request, user)) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(request.email());

        return new UserLoginResponse(token, "Bearer");
    }

    private boolean validatePassword(UserLoginRequest request, User user) {
        boolean ok = passwordEncoder.matches(request.password(), user.getPasswordHash());
        return ok;
    }

    public UserMeResponse whoami(String request) {
        User user = userRepository.findByEmail(request)
                .orElseThrow(() -> new UserNotFoundException("no iser with email:" + request));
        return new UserMeResponse(user.getId(), user.getName(), user.getEmail());
    }

}
