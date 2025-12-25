package com.anu.aijobmatching.job;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anu.aijobmatching.job.dto.JobCreateRequest;
import com.anu.aijobmatching.job.dto.JobResponse;
import com.anu.aijobmatching.job.dto.JobUpdateRequest;
import com.anu.aijobmatching.job.exception.ForbiddenResourceException;
import com.anu.aijobmatching.job.exception.JobNotFoundException;
import com.anu.aijobmatching.user.User;
import com.anu.aijobmatching.user.UserRepository;



@Service
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public JobResponse create(String ownerName, JobCreateRequest req) {
        User owner = userRepository.findByEmailIgnoreCase(ownerName).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Job job = Job.builder()
                        .owner(owner)
                        .title(req.title())
                        .company(req.company())
                        .location(req.location())
                        .employementType(req.employementType())
                        .seniority(req.seniority())
                        .description(req.description())
                        .requiredSkills(req.requirements())
                        .skillsCV(req.skillsCV())
                        .build();

        Job savedJob = jobRepository.save(job);

        return toResponse(savedJob);

    }

    @Transactional
    public JobResponse getMine(String ownerName, Long id){
        Job job = jobRepository.findById(id).orElseThrow(()-> new JobNotFoundException("Job not found"));
        assertOwner(ownerName, job);
        return toResponse(job);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> listMine(String ownerEmail) {
        return jobRepository.findByOwnerEmailOrderByCreatedAtDesc(ownerEmail)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public JobResponse updateMine(String ownerEmail, Long jobId, JobUpdateRequest req) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found: " + jobId));
        assertOwner(ownerEmail, job);

        if (req.title() != null) job.setTitle(req.title());
        if (req.company() != null) job.setCompany(req.company());
        if (req.location() != null) job.setLocation(req.location());
        if (req.employementType() != null) job.setEmployementType(req.employementType());
        if (req.seniority() != null) job.setSeniority(req.seniority());
        if (req.description() != null) job.setDescription(req.description());
        if (req.requirements() != null) job.setRequiredSkills(req.requirements());
        if (req.skillsCV() != null) job.setSkillsCV(req.skillsCV());

        Job saved = jobRepository.save(job);
        return toResponse(saved);
    }

    @Transactional
    public void deleteMine(String ownerEmail, Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found: " + jobId));
        assertOwner(ownerEmail, job);
        jobRepository.delete(job);
    }

    public Page<JobResponse> listMyJobs(String ownerEmail, String q, String company, String skillsCV, Pageable pageable) {
        // Implement search logic here, possibly using Specifications or Criteria API
        // For simplicity, let's assume we have a method in jobRepository to handle this
        Set<String> skills = tokenizeCsvOrEmpty(skillsCV);
        Specification<Job> spec = Specification.where(JobSpecifications.ownerEqualsEmail(ownerEmail));
        if(q!=null && !q.isBlank()) {
            spec = spec.and(JobSpecifications.qContainsIgnoreCase(q));
        }
        if(company!=null && !company.isBlank()) {
            spec = spec.and(JobSpecifications.companyContainsIgnoreCase(company));  
        }
        if(skills!=null && !skills.isEmpty()) {
            spec = spec.and(JobSpecifications.skillsAnyContainsIgnoreCase(skills));
        }

        return jobRepository.findAll(spec, pageable).map(this::toResponse);
    }

    private Set<String> tokenizeCsvOrEmpty(String csv) {
        if (csv == null || csv.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
    }

    private void assertOwner(String email, Job job) {
        String ownerEmail = job.getOwner().getEmail();
        if (!email.equalsIgnoreCase(ownerEmail)) {
            throw new ForbiddenResourceException("Not allowed to access job " + job.getId());
        }
    }

    private JobResponse toResponse(Job savedJob) {
        return new JobResponse(
                savedJob.getId(),
                savedJob.getTitle(),
                savedJob.getDescription(),
                savedJob.getRequiredSkills(),
                savedJob.getLocation(),
                savedJob.getEmployementType(),
                savedJob.getSeniority(),
                savedJob.getSkillsCV(),savedJob.getCompany(), savedJob.getCreatedAt(),savedJob.getUpdatedAt()
        );
    }
}
