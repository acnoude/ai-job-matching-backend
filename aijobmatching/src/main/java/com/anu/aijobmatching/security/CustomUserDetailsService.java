package com.anu.aijobmatching.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.anu.aijobmatching.user.User;
import com.anu.aijobmatching.user.UserRepository;

import io.jsonwebtoken.lang.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emaString) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(emaString)
                .orElseThrow(() -> new UsernameNotFoundException("User not found:" + emaString));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPasswordHash(),
                Collections.emptyList());
    }

}
