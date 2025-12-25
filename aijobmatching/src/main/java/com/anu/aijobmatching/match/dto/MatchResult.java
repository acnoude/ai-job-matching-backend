package com.anu.aijobmatching.match.dto;

public record MatchResult(
        Long jobId,
        String title,
        double score
) {
} 