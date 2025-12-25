package com.anu.aijobmatching.match;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anu.aijobmatching.match.dto.MatchResponse;
import com.anu.aijobmatching.match.dto.MatchResult;

@RestController
@RequestMapping("/api/match")
public class MatchController {
    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // GET /api/match/resume/{resumeId}
@GetMapping("/resume/{resumeId}")
    public ResponseEntity<Page<MatchResponse>> match(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long resumeId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                matchService.matchMyResumetoJobs(userDetails.getUsername(), resumeId, pageable)
        );
    }

    // @GetMapping("/resume/{id}")
    // public Page<MatchResult> match(
    //         @PathVariable Long id,
    //         @PageableDefault(size = 10) Pageable pageable
    // ) {
    //     return matchService.matchResume(id, pageable);
    // }
}
