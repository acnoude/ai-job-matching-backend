package com.anu.aijobmatching.resume.dto;

public record StoredFile(
        String originalFileName,
        String storedPath,
        String contentType,
        long sizeBytes
) {
}