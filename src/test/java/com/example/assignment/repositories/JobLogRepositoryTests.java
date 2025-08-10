package com.example.assignment.repositories;

import com.example.assignment.config.TestConfig;
import com.example.assignment.domains.JobLog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Import(TestConfig.class)
public class JobLogRepositoryTests {
    @Autowired
    private JobLogRepository repository;

    @Test
    public void GivenJob_WhenSave_ThenShouldReturnSavedJob() {
        JobLog job = JobLog.builder()
                .fileName("file_1")
                .status("pending")
                .build();

        JobLog savedJob = repository.save(job);

        Assertions.assertNotNull(savedJob);
        Assertions.assertEquals(job.getFileName(), savedJob.getFileName());
        Assertions.assertEquals(job.getStatus(), savedJob.getStatus());
    }

    @Test
    public void GivenFilename_WhenFindByFilename_ThenShouldReturnJob() {
        JobLog job = JobLog.builder()
                .fileName("file_1")
                .status("pending")
                .build();
        repository.save(job);

        JobLog savedJob = repository.findByFileName(job.getFileName())
                .orElse(null);

        Assertions.assertNotNull(savedJob);
        Assertions.assertEquals(job.getFileName(), savedJob.getFileName());
    }
}
