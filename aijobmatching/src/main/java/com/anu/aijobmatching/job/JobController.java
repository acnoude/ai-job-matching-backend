package com.anu.aijobmatching.job;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anu.aijobmatching.job.dto.JobCreateRequest;
import com.anu.aijobmatching.job.dto.JobResponse;
import com.anu.aijobmatching.job.dto.JobUpdateRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/jobs")
@Validated
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Implement the necessary methods to handle job-related requests

    @PostMapping
    public ResponseEntity<JobResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody JobCreateRequest req
    ) {
        return ResponseEntity.ok(jobService.create(userDetails.getUsername(), req));
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> listMine(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobService.listMine(userDetails.getUsername()));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getMine(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long jobId
    ) {
        return ResponseEntity.ok(jobService.getMine(userDetails.getUsername(), jobId));
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponse> updateMine(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long jobId,
            @RequestBody JobUpdateRequest req
    ) {
        return ResponseEntity.ok(jobService.updateMine(userDetails.getUsername(), jobId, req));
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteMine(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long jobId
    ) {
        jobService.deleteMine(userDetails.getUsername(), jobId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<JobResponse>> listMyJobs(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String q,
            @RequestParam(required =  false) String company,
            @RequestParam(required = false) String skillsCsv,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(jobService.listMyJobs(
                userDetails.getUsername(),
                q,
                company,
                skillsCsv,
                pageable
        ));
    }
}
