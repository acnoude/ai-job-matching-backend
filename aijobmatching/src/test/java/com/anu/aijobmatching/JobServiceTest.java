package com.anu.aijobmatching;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.anu.aijobmatching.job.JobService;
import com.anu.aijobmatching.job.dto.JobCreateRequest;
import com.anu.aijobmatching.job.dto.JobResponse;

@SpringBootTest
public class JobServiceTest {
     @Autowired
    JobService jobService;

    @Test
    void createJob_success() {
        JobCreateRequest req =
            new JobCreateRequest("Backend Engineer", "java,spring", null, null, null, null, null, null);

        JobResponse res = jobService.create("anudevara",req);

        assertNotNull(res.id());
    }
}
