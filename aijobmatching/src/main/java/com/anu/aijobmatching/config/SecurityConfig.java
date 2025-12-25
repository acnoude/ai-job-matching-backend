package com.anu.aijobmatching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.anu.aijobmatching.security.JwtAuthenticationFilter;
import com.anu.aijobmatching.security.RestAuthenticationEntryPoint;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http
        // .csrf(csrf -> csrf.disable())
        // .authorizeHttpRequests(
        // auth -> auth.requestMatchers("/api/users/register",
        // "/api/users/login").permitAll().anyRequest()
        // .authenticated());

        // return http.build();

        // http.csrf(csrf -> csrf.disable())
        // .sessionManagement(sm ->
        // sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // .authorizeHttpRequests(auth -> auth.requestMatchers("/api/users/register",
        // "/api/users/login",
        // "/h2-console/**").permitAll().anyRequest().authenticated())
        // .addFilterBefore(jwtAuthenticationFilter,
        // UsernamePasswordAuthenticationFilter.class);

        // http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        // return http.build();

        // http.csrf(csrf -> csrf.disable())
        //         .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        //         .exceptionHandling(eh -> eh.authenticationEntryPoint(authenticationEntryPoint))
        //         .authorizeHttpRequests(auth -> auth
        //                 .requestMatchers("/api/admin/**").hasRole("ADMIN")
        //                 .requestMatchers("/api/users/login", "/h2-console/**", "/api/health").hasRole("USER", "ADMIN")
        //                 .anyRequest().authenticated())
        //         .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        http.csrf(csrf -> csrf.disable()).sessionManagement(sm-> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(eh-> eh.authenticationEntryPoint(authenticationEntryPoint)
        .accessDeniedHandler((req,res, ex) ->{
            res.setStatus(403);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"FORBIDDEN\",\"message\":\"Access denied\"}");
        })
        )
        .authorizeHttpRequests(auth -> auth.requestMatchers("/api/users/register", "/api/users/login", "/api/health", "/h2-console/**", "/api/resume/**").permitAll()
        .requestMatchers("/api/admin/**").hasAllRoles("ADMIN", "USER")
        .anyRequest().authenticated()
        ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
