package com.anu.aijobmatching.match.dto;

public record MatchResponse(
       Long jobId,
        String title,
        String company,
        double score,
        String matchedKeywords
) {
    
}
