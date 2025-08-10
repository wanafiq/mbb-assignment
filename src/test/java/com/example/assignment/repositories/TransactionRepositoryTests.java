package com.example.assignment.repositories;

import com.example.assignment.config.TestConfig;
import com.example.assignment.domains.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Import(TestConfig.class)
public class TransactionRepositoryTests {
    @Autowired
    private TransactionRepository repository;

    @Test
    public void GivenTransaction_WhenSave_ThenShouldReturnSavedJob() {
        Transaction trx = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountNo("8872838283")
                .amount(new BigDecimal("1243.00"))
                .description("3rd Party FUND TRANSFER")
                .date(LocalDate.parse("2019-08-11", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .time(LocalTime.parse("11:11:11", DateTimeFormatter.ofPattern("HH:mm:ss")))
                .customerId(Long.parseLong("222"))
                .build();

        Transaction savedTrx = repository.save(trx);

        Assertions.assertNotNull(savedTrx);
        Assertions.assertNotNull(savedTrx.getId());
        Assertions.assertEquals(trx.getAccountNo(), savedTrx.getAccountNo());
        Assertions.assertEquals(trx.getAmount(), savedTrx.getAmount());
        Assertions.assertEquals(trx.getDescription(), savedTrx.getDescription());
        Assertions.assertEquals(trx.getDate(), savedTrx.getDate());
        Assertions.assertEquals(trx.getTime(), savedTrx.getTime());
        Assertions.assertEquals(trx.getCustomerId(), savedTrx.getCustomerId());
    }

    @Test
    public void GivenTransactions_WhenSaveAll_ThenShouldReturnAllTransactions() {
        List<Transaction> trxs = new ArrayList<>();

        Transaction trx1 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountNo("8872838283")
                .amount(new BigDecimal("1243.00"))
                .description("3rd Party FUND TRANSFER")
                .date(LocalDate.parse("2019-08-11", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .time(LocalTime.parse("11:11:11", DateTimeFormatter.ofPattern("HH:mm:ss")))
                .customerId(Long.parseLong("222"))
                .build();

        Transaction trx2 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountNo("8872838299")
                .amount(new BigDecimal("1121223.00"))
                .description("FUND TRANSFER")
                .date(LocalDate.parse("2019-09-11", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .time(LocalTime.parse("11:11:11", DateTimeFormatter.ofPattern("HH:mm:ss")))
                .customerId(Long.parseLong("222"))
                .build();

        Transaction trx3 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountNo("8872838299")
                .amount(new BigDecimal("1223233.00"))
                .description("ATM WITHDRWAL")
                .date(LocalDate.parse("2019-09-11", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .time(LocalTime.parse("11:11:11", DateTimeFormatter.ofPattern("HH:mm:ss")))
                .customerId(Long.parseLong("222"))
                .build();

        trxs.add(trx1);
        trxs.add(trx2);
        trxs.add(trx3);
        List<Transaction> savedTrxs = repository.saveAll(trxs);

        Assertions.assertEquals(trxs.size(), savedTrxs.size());
    }
}
