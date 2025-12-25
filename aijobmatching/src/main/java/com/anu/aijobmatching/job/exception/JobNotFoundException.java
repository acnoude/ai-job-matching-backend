package com.anu.aijobmatching.job.exception;

public class JobNotFoundException extends ForbiddenResourceException {
    public JobNotFoundException(String message) {
        super(message);
    }
    
}
