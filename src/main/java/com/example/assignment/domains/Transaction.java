package com.example.assignment.domains;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction extends Audit {
    @Id
    private String id;

    @Column(name = "account_no", length = 20, nullable = false)
    private String accountNo;

    @Column(name = "trx_amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "trx_date",  nullable = false)
    private LocalDate date;

    @Column(name = "trx_time", nullable = false)
    private LocalTime time;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;
}
