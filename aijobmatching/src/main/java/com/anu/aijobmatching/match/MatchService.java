package com.anu.aijobmatching.match;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.anu.aijobmatching.job.Job;
import com.anu.aijobmatching.job.JobRepository;
import com.anu.aijobmatching.match.dto.MatchResponse;
import com.anu.aijobmatching.resume.Resume;
import com.anu.aijobmatching.resume.ResumeRepository;

@Service
public class MatchService {

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;

    public MatchService(ResumeRepository resumeRepository, JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
    }

    public Page<MatchResponse> matchMyResumetoJobs(String ownerEmail, Long resumeId, Pageable pageable) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (resume.getOwner() == null || resume.getOwner().getEmail() == null
                || !resume.getOwner().getEmail().equalsIgnoreCase(ownerEmail)) {
            throw new RuntimeException("Resume does not belong to the user");
        }

        Set<String> resumeTokens = tokenizeCsvOrEmpty(resume.getKeywordsCsv());

        List<Job> jobs = jobRepository.findByOwnerEmailOrderByCreatedAtDesc(ownerEmail);

        List<MatchResponse> all = jobs.stream()
                .map(job -> score(job, resumeTokens))
                .sorted(Comparator.comparingDouble(MatchResponse::score).reversed())
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        if (start > end) start = end; // safe guard

        return new PageImpl<>(all.subList(start, end), pageable, all.size());
    }

    private MatchResponse score(Job job, Set<String> resumeTokens) {
        Set<String> jobTokens = new HashSet<>();
        jobTokens.addAll(tokenizeCsvOrEmpty(job.getSkillsCV()));
        jobTokens.addAll(tokenizeFreeText(job.getDescription()));
        jobTokens.addAll(tokenizeFreeText(job.getTitle()));
        jobTokens.addAll(tokenizeFreeText(job.getRequiredSkills()));

        Set<String> matched = new HashSet<>(resumeTokens);
        matched.retainAll(jobTokens);

        double denom = Math.max(1, resumeTokens.size());
        double score = ((double) matched.size()) / denom;

        String matchedKeywords = matched.stream().sorted().collect(Collectors.joining(", "));

        return new MatchResponse(
                job.getId(),
                job.getTitle(),
                job.getCompany(),
                score,
                matchedKeywords
        );
    }

    private Set<String> tokenizeFreeText(String text) {
        if (text == null || text.isBlank()) return Set.of();
        return Arrays.stream(text.toLowerCase().split("[^a-z0-9+.#]+"))
                .map(String::trim)
                .filter(s -> s.length() >= 2)
                .collect(Collectors.toSet());
    }

    private Set<String> tokenizeCsvOrEmpty(String csv) {
        if (csv == null || csv.isBlank()) return Set.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
    }
}
