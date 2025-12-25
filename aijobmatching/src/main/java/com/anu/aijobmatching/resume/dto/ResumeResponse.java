package com.anu.aijobmatching.resume.dto;

import java.time.LocalDateTime;

public record ResumeResponse(
        Long id,
        String originalFileName,
        String contentType,
        long sizeBytes,
        LocalDateTime uploadedAt
){}
