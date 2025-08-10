package com.example.assignment.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class TransactionResponseDTO {
    private String id;
    private String accountNo;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private Long customerId;
}