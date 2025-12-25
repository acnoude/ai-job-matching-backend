package com.anu.aijobmatching.resume;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.anu.aijobmatching.resume.dto.ResumeResponse;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResumeResponse> upload(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(resumeService.upload(userDetails.getUsername(), file));
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponse>> listMine(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(resumeService.listMine(userDetails.getUsername()));
    }

    @PutMapping("/{resumeId}/keywords")
    public ResponseEntity<ResumeResponse> setKeywords(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long resumeId,
            @RequestParam("keywordsCsv") String keywordsCsv
    ) {
        // implement a small method in ResumeService to update keywordsCsv
        return ResponseEntity.ok(resumeService.updateKeywords(userDetails.getUsername(), resumeId, keywordsCsv));
    }
}
