package com.anu.aijobmatching.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.anu.aijobmatching.user.User;
import com.anu.aijobmatching.user.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emaString) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(emaString)
                .orElseThrow(() -> new UsernameNotFoundException("User not found:" + emaString));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }

}
