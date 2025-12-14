package com.anu.aijobmatching.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anu.aijobmatching.user.dto.UserLoginRequest;
import com.anu.aijobmatching.user.dto.UserLoginResponse;
import com.anu.aijobmatching.user.dto.UserMeResponse;
import com.anu.aijobmatching.user.dto.UserRegisterRequest;
import com.anu.aijobmatching.user.dto.UserRegisterResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> whoami(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.whoami(userDetails.getUsername()));
    }
}
