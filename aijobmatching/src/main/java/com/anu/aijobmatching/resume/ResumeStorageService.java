package com.anu.aijobmatching.resume;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.anu.aijobmatching.resume.dto.StoredFile;

@Service
public class ResumeStorageService {
    private final Path baseDir;

    public ResumeStorageService(@Value("${app.storage.resume-dir:uploads/resumes}") String resumeDir) {
        this.baseDir = Paths.get(resumeDir);
    }

    public StoredFile store(MultipartFile file) {
        // Implement the logic to store the uploaded resume file in the specified directory
        // Return a StoredFile object containing information about the stored file
        // Example:
        // String fileName = file.getOriginalFilename();
        // Path filePath = baseDir.resolve(fileName);
        // file.transferTo(filePath);
        // return new StoredFile(fileName, filePath.toString());

        try{
            Files.createDirectories(baseDir);
            String originalFileName = file.getOriginalFilename() == null ? "resume": file.getOriginalFilename();
            String ext = "";
            int idx = originalFileName.lastIndexOf('.');
            if(idx>=0){
                ext = originalFileName.substring(idx);
            }

            String name = UUID.randomUUID() + ext;
            Path target = baseDir.resolve(name).normalize();

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return new StoredFile(originalFileName, target.toString(), file.getContentType() == null?
                    "application/octet-stream": file.getContentType(),  file.getSize());
        }catch(IOException e){
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
