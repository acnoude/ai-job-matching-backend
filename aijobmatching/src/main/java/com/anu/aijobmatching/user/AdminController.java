package com.anu.aijobmatching.user;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PreAuthorize("hasRole("ADMIN")")
    @GetMapping("/health")
    public String adminHealth() {
        return "ADMIN OK";
    }
}
