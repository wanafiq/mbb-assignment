package com.example.assignment.services;

import com.example.assignment.domains.JobLog;
import com.example.assignment.domains.Transaction;
import com.example.assignment.repositories.JobLogRepository;
import com.example.assignment.repositories.TransactionRepository;
import com.example.assignment.web.exceptions.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionProcessor {
    private static final int BATCH_SIZE = 10;
    
    private final ResourcePatternResolver resourcePatternResolver;
    private final JobLogRepository jobRepo;
    private final TransactionRepository trxRepo;

    public void run() {
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:data/*");

            for (Resource resource : resources) {
                if (resource.exists() && resource.isReadable()) {
                    Optional<JobLog> job = jobRepo.findByFileName(resource.getFilename());
                    if (job.isPresent()) {
                        continue;
                    }
                    process(resource);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalException();
        }
    }

    private void process(Resource resource) {
        log.info("Processing transaction file: {}", resource.getFilename());

        JobLog job = JobLog.builder()
                .fileName(resource.getFilename())
                .status("pending")
                .build();
        jobRepo.save(job);

        List<Transaction> transactions = new ArrayList<>();
        int totalProcessed = 0;
        int totalErrors = 0;

        try (
                InputStreamReader isr = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(isr)
        ) {

            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Skip header line if it contains column names
                if (isFirstLine && isHeader(line)) {
                    isFirstLine = false;
                    continue;
                }
                isFirstLine = false;

                String[] parts = line.split("\\|");
                String accountNo = parts[0].trim();
                String amount = parts[1].trim();
                String description = parts[2].trim();
                String date = parts[3].trim();
                String time = parts[4].trim();
                String customerId = parts[5].trim();

                try {
                    Transaction trx = Transaction.builder()
                            .id(UUID.randomUUID().toString())
                            .accountNo(accountNo)
                            .amount(new BigDecimal(amount))
                            .description(description)
                            .date(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .time(LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss")))
                            .customerId(Long.parseLong(customerId))
                            .build();

                    transactions.add(trx);

                    // Process in batches
                    if (transactions.size() >= BATCH_SIZE) {
                        trxRepo.saveAll(transactions);
                        totalProcessed += transactions.size();
                        log.info("Processed batch: {} transactions", transactions.size());
                        
                        // Update job progress
                        job.setStatus("processing");
                        jobRepo.save(job);
                        
                        transactions.clear();
                    }

                } catch (Exception e) {
                    log.error("Error parsing line - {}: {}", line, e.getMessage());
                    totalErrors++;
                }
            }

            // Save remaining transactions
            if (!transactions.isEmpty()) {
                trxRepo.saveAll(transactions);
                totalProcessed += transactions.size();
                log.info("Processed final batch: {} transactions", transactions.size());
            }

            job.setStatus("success");
            jobRepo.save(job);

            log.info("File processing completed. Total processed: {}, Total errors: {}", totalProcessed, totalErrors);
        } catch (Exception e) {
            log.error("Error while processing file {}: {}", resource.getFilename(), e.getMessage());
            job.setStatus("failed");
            jobRepo.save(job);
            throw new InternalException();
        }
    }

    private boolean isHeader(String line) {
        String lowerLine = line.toLowerCase();
        return lowerLine.contains("account") ||
                lowerLine.contains("amount") ||
                lowerLine.contains("description") ||
                lowerLine.contains("date") ||
                lowerLine.contains("time") ||
                lowerLine.contains("customer");
    }
}
