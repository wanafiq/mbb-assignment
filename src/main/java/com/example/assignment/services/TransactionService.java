package com.example.assignment.services;

import com.example.assignment.domains.Transaction;
import com.example.assignment.repositories.TransactionRepository;
import com.example.assignment.services.specifications.TransactionSpecification;
import com.example.assignment.web.dto.TransactionSearchDTO;
import com.example.assignment.web.exceptions.ResourceLockedException;
import com.example.assignment.web.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final DistributedLock distributedLock;

    public Page<Transaction> search(TransactionSearchDTO searchDTO, Pageable pageable) {
        Specification<Transaction> spec = TransactionSpecification.withFilters(searchDTO);
        return transactionRepository.findAll(spec, pageable);
    }

    @Transactional
    public Transaction updateDescription(String id, String description) {
        String lockKey = "transaction:" + id;
        String lockValue = Thread.currentThread().getName() + ":" + System.currentTimeMillis();

        if (!distributedLock.waitForLock(lockKey, lockValue)) {
            throw new ResourceLockedException("Unable to acquire lock for transaction. Please try again later.");
        }
        
        try {
            Transaction transaction = transactionRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

            transaction.setDescription(description);

            return transactionRepository.save(transaction);
        } finally {
            distributedLock.releaseLock(lockKey, lockValue);
        }
    }
}