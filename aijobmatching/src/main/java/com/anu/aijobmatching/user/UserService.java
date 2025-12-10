package com.anu.aijobmatching.user;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.anu.aijobmatching.user.dto.UserRegisterRequest;
import com.anu.aijobmatching.user.dto.UserRegisterResponse;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRegisterResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder().name(request.name()).email(request.email())
                .passwordHash(passwordEncoder.encode(request.password())).createdAt(LocalDateTime.now()).build();

        User saved = userRepository.save(user);

        return new UserRegisterResponse(saved.getId(), saved.getName(), saved.getEmail());
    }
}
