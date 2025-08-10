package com.example.assignment.jobs;

import com.example.assignment.services.TransactionProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JobScheduler {
    private final TransactionProcessor processor;

    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    public void everyMinutes() {
        processor.run();
    }

    @Scheduled(cron = "0 0 0 * * * ")
    public void everyDayAtMidnight() {
        processor.run();
    }
}
