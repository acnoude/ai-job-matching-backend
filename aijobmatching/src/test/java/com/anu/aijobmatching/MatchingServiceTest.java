package com.anu.aijobmatching;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.anu.aijobmatching.job.Job;
import com.anu.aijobmatching.job.JobRepository;
import com.anu.aijobmatching.match.MatchService;
import com.anu.aijobmatching.match.dto.MatchResponse;
import com.anu.aijobmatching.resume.Resume;
import com.anu.aijobmatching.resume.ResumeRepository;
import com.anu.aijobmatching.user.User;

class MatchServiceTest {

    @Test
    void matchMyResumetoJobs_returnsSortedByScore_desc() {
        ResumeRepository resumeRepo = mock(ResumeRepository.class);
        JobRepository jobRepo = mock(JobRepository.class);

        MatchService service = new MatchService(resumeRepo, jobRepo);

        User owner = new User();
        owner.setEmail("anu921@gmail.com");

        Resume resume = new Resume();
        resume.setId(1L);
        resume.setOwner(owner);
        resume.setKeywordsCsv("java,spring,aws,kafka");

        Job j1 = new Job();
        j1.setId(10L);
        j1.setOwner(owner);
        j1.setTitle("Java Backend Engineer");
        j1.setCompany("A");
        j1.setRequiredSkills("java spring");
        j1.setDescription("");
        j1.setSkillsCV("");
        // j1.setCreatedAt(Instant.now());

        Job j2 = new Job();
        j2.setId(11L);
        j2.setOwner(owner);
        j2.setTitle("Python ML Engineer");
        j2.setCompany("B");
        j2.setRequiredSkills("python pytorch");
        j2.setDescription("");
        j2.setSkillsCV("");
        // j2.setCreatedAt(Instant.now());

        when(resumeRepo.findById(1L)).thenReturn(java.util.Optional.of(resume));
        when(jobRepo.findByOwnerEmailOrderByCreatedAtDesc("anu921@gmail.com"))
                .thenReturn(List.of(j1, j2));

        Page<MatchResponse> page = service.matchMyResumetoJobs(
                "anu921@gmail.com",
                1L,
                PageRequest.of(0, 10)
        );

        assertEquals(2, page.getTotalElements());
        assertEquals(10L, page.getContent().get(0).jobId()); // Java job should rank first
        assertTrue(page.getContent().get(0).score() > page.getContent().get(1).score());
    }

    @Test
    void matchMyResumetoJobs_throwsIfResumeNotOwnedByUser() {
        ResumeRepository resumeRepo = mock(ResumeRepository.class);
        JobRepository jobRepo = mock(JobRepository.class);

        MatchService service = new MatchService(resumeRepo, jobRepo);

        User owner = new User();
        owner.setEmail("someoneelse@gmail.com");

        Resume resume = new Resume();
        resume.setId(1L);
        resume.setOwner(owner);
        resume.setKeywordsCsv("java");

        when(resumeRepo.findById(1L)).thenReturn(java.util.Optional.of(resume));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.matchMyResumetoJobs("anu921@gmail.com", 1L, PageRequest.of(0, 10))
        );

        assertTrue(ex.getMessage().toLowerCase().contains("does not belong"));
    }
}
