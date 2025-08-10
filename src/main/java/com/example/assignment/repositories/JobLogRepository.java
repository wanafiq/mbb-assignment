package com.example.assignment.repositories;

import com.example.assignment.domains.JobLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobLogRepository extends JpaRepository<JobLog, Long> {
    Optional<JobLog> findByFileName(String name);
}
