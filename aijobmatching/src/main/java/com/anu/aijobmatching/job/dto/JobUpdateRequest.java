package com.anu.aijobmatching.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobUpdateRequest(
       @NotBlank @Size(max = 200)String title,
     @Size(max = 5000) String description,
    String location,
    String seniority,
    @NotBlank @Size(max = 200) String company,
    String employementType,
    @Size(max = 2000) String requirements,
    @Size(max = 1000) String skillsCV
) {
}