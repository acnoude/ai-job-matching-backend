package com.anu.aijobmatching.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobCreateRequest(
    @NotBlank @Size(max = 200) String title,
    @NotBlank @Size(max = 5000) String description,
    String location,
    String seniority,
    @NotBlank @Size(max = 200) String company,
    String employementType,
    @Size(max = 2000) String  requirements,
    @Size(max = 1000) String skillsCV
) {
}