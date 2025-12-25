package com.anu.aijobmatching.resume;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.anu.aijobmatching.resume.dto.ResumeResponse;
import com.anu.aijobmatching.resume.dto.StoredFile;
import com.anu.aijobmatching.user.User;
import com.anu.aijobmatching.user.UserRepository;
import com.anu.aijobmatching.user.exception.UserNotFoundException;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ResumeStorageService resumeStorageService;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository, ResumeStorageService resumeStorageService) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.resumeStorageService = resumeStorageService;
    }

    public ResumeResponse upload(String ownerEmail, MultipartFile file){
        User owner = userRepository.findByEmailIgnoreCase(ownerEmail).orElseThrow(() -> new UserNotFoundException("no user found"));
        if(file.isEmpty()){
            throw new IllegalArgumentException("file is empty");
        }

        StoredFile stored = resumeStorageService.store(file);
        Resume resume = Resume.builder().owner(owner).originalFileName(stored.originalFileName()).storedPath(stored.storedPath()).contentType(stored.contentType()).sizeBytes(stored.sizeBytes()).build();
        Resume saved = resumeRepository.save(resume);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ResumeResponse> listMine(String ownerEmail) {
        return resumeRepository.findByOwnerEmailOrderByUploadedAtDesc(ownerEmail)
                .stream().map(this::toResponse).toList();
    }

    private ResumeResponse toResponse(Resume r) {
               return new ResumeResponse(r.getId(), r.getOriginalFileName(), r.getContentType(), r.getSizeBytes(), r.getUploadedAt());

    }

    @Transactional
    public ResumeResponse updateKeywords(String ownerEmail, Long resumeId, String keywordsCsv) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new RuntimeException("Resume not found"));
        if(!resume.getOwner().getEmail().equalsIgnoreCase(ownerEmail)){
            throw new RuntimeException("Resume does not belong to the user");
        }
        resume.setKeywordsCsv(keywordsCsv);
        Resume updated = resumeRepository.save(resume);
        return toResponse(updated);
    }
}