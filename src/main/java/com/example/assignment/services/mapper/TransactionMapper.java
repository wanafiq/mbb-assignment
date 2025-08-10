package com.example.assignment.services.mapper;

import com.example.assignment.domains.Transaction;
import com.example.assignment.web.dto.TransactionResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponseDTO toDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .accountNo(transaction.getAccountNo())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .date(transaction.getDate())
                .time(transaction.getTime())
                .customerId(transaction.getCustomerId())
                .build();
    }
}