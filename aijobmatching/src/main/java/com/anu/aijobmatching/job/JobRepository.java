package com.anu.aijobmatching.job;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
public interface JobRepository extends JpaRepository<Job, Long>,  JpaSpecificationExecutor<Job> {
   List<Job> findByOwnerEmailOrderByCreatedAtDesc(String email);
}
