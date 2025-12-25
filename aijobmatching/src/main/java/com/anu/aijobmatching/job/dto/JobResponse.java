package com.anu.aijobmatching.job.dto;

import java.time.LocalDateTime;

public record JobResponse(
    Long id,
    String title,
    String description,
    String location,
    String seniority,
    String company,
    String employementType,
    String requirements,
    String skillsCV,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}