package com.example.assignment.services;

import com.example.assignment.domains.Transaction;
import com.example.assignment.repositories.TransactionRepository;
import com.example.assignment.web.dto.TransactionSearchDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private DistributedLock distributedLock;

    @InjectMocks
    private TransactionService transactionService;

    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        Transaction trx1 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountNo("8872838283")
                .amount(BigDecimal.valueOf(123.00))
                .description("FUND TRANSFER")
                .date(LocalDate.of(2019, 9, 12))
                .time(LocalTime.of(11, 11, 11))
                .customerId(222L)
                .build();

        Transaction trx2 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountNo("8872838283")
                .amount(BigDecimal.valueOf(1123.00))
                .description("ATM WITHDRWAL")
                .date(LocalDate.of(2019, 9, 11))
                .time(LocalTime.of(11, 11, 11))
                .customerId(222L)
                .build();

        Transaction trx3 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountNo("8872838283")
                .amount(BigDecimal.valueOf(1233.00))
                .description("3rd Party FUND TRANSFER")
                .date(LocalDate.of(2019, 11, 11))
                .time(LocalTime.of(11, 11, 11))
                .customerId(222L)
                .build();

        Transaction trx4 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountNo("6872838260")
                .amount(BigDecimal.valueOf(1.00))
                .description("BILL PAYMENT")
                .date(LocalDate.of(2019, 9, 11))
                .time(LocalTime.of(11, 11, 11))
                .customerId(333L)
                .build();

        Transaction trx5 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountNo("6872838260")
                .amount(BigDecimal.valueOf(1223.00))
                .description("BILL PAYMENT")
                .date(LocalDate.of(2019, 9, 12))
                .time(LocalTime.of(11, 11, 11))
                .customerId(333L)
                .build();

        Transaction trx6 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountNo("8872838299")
                .amount(BigDecimal.valueOf(11123.00))
                .description("FUND TRANSFER")
                .date(LocalDate.of(2019, 9, 11))
                .time(LocalTime.of(11, 11, 11))
                .customerId(222L)
                .build();

        Transaction trx7 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountNo("8872838299")
                .amount(BigDecimal.valueOf(12223.00))
                .description("BILL PAYMENT")
                .date(LocalDate.of(2019, 9, 11))
                .time(LocalTime.of(11, 11, 11))
                .customerId(222L)
                .build();

        Transaction trx8 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountNo("8872838299")
                .amount(BigDecimal.valueOf(1223233.00))
                .description("ATM WITHDRWAL")
                .date(LocalDate.of(2019, 9, 11))
                .time(LocalTime.of(11, 11, 11))
                .customerId(222L)
                .build();

        Transaction trx9 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountNo("8872838299")
                .amount(BigDecimal.valueOf(1223233.00))
                .description("3rd Party FUND TRANSFER")
                .date(LocalDate.of(2019, 9, 11))
                .time(LocalTime.of(11, 11, 11))
                .customerId(222L)
                .build();

        Transaction trx10 = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountNo("6872838260")
                .amount(BigDecimal.valueOf(99123.00))
                .description("FUND TRANSFER")
                .date(LocalDate.of(2019, 9, 14))
                .time(LocalTime.of(11, 11, 11))
                .customerId(333L)
                .build();

        transactions = Arrays.asList(trx1, trx2, trx3, trx4, trx5, trx6, trx7, trx8, trx9, trx10);
    }

    @Test
    public void GivenEmptySearchDTO_WhenSearch_ThenShouldReturnAllTransactions() {
        TransactionSearchDTO searchDTO = new TransactionSearchDTO();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> expectedPage = new PageImpl<>(transactions, pageable, 10);

        when(transactionRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(expectedPage);

        Page<Transaction> result = transactionService.search(searchDTO, pageable);

        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals(10, result.getContent().size());
        verify(transactionRepository, times(1))
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    public void GivenCustomerIdFilter_WhenSearch_ThenShouldReturnFilteredTransactions() {
        TransactionSearchDTO searchDTO = TransactionSearchDTO.builder()
                .customerId(222L)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        List<Transaction> trxs = transactions.stream()
                .filter(t -> t.getCustomerId().equals(222L))
                .toList();
        Page<Transaction> expectedPage = new PageImpl<>(trxs, pageable, 7);

        when(transactionRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(expectedPage);

        Page<Transaction> result = transactionService.search(searchDTO, pageable);

        assertNotNull(result);
        assertEquals(7, result.getTotalElements());
        assertEquals(7, result.getContent().size());

        result.getContent().forEach(transaction -> 
            assertEquals(222L, transaction.getCustomerId())
        );

        verify(transactionRepository, times(1))
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    public void GivenAccountNoFilter_WhenSearch_ThenShouldReturnFilteredTransactions() {
        TransactionSearchDTO searchDTO = TransactionSearchDTO.builder()
                .accountNo("8872838283")
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        List<Transaction> trxs = transactions.stream()
                .filter(t -> t.getAccountNo().equals("8872838283"))
                .toList();
        Page<Transaction> expectedPage = new PageImpl<>(trxs, pageable, 3);

        when(transactionRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(expectedPage);

        Page<Transaction> result = transactionService.search(searchDTO, pageable);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());

        result.getContent().forEach(transaction -> 
            assertEquals("8872838283", transaction.getAccountNo())
        );

        verify(transactionRepository, times(1))
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    public void GivenDescriptionFilter_WhenSearch_ThenShouldReturnFilteredTransactions() {
        TransactionSearchDTO searchDTO = TransactionSearchDTO.builder()
                .description("FUND TRANSFER")
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        List<Transaction> trxs = transactions.stream()
                .filter(t -> t.getDescription().toUpperCase().contains("FUND"))
                .toList();
        Page<Transaction> expectedPage = new PageImpl<>(trxs, pageable, 3);

        when(transactionRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(expectedPage);

        Page<Transaction> result = transactionService.search(searchDTO, pageable);

        assertNotNull(result);
        assertEquals(5, result.getTotalElements());
        assertEquals(5, result.getContent().size());

        result.getContent().forEach(transaction -> 
            assertTrue(transaction.getDescription().toUpperCase().contains("FUND TRANSFER"))
        );

        verify(transactionRepository, times(1))
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    public void GivenMultipleFilters_WhenSearch_ThenShouldReturnFilteredTransactions() {
        TransactionSearchDTO searchDTO = TransactionSearchDTO.builder()
                .customerId(222L)
                .accountNo("8872838283")
                .description("FUND TRANSFER")
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        List<Transaction> trxs = transactions.stream()
                .filter(t -> t.getCustomerId().equals(222L))
                .filter(t -> t.getAccountNo().equals("8872838283"))
                .filter(t -> t.getDescription().toUpperCase().contains("FUND TRANSFER"))
                .toList();
        Page<Transaction> expectedPage = new PageImpl<>(trxs, pageable, 2);

        when(transactionRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(expectedPage);

        Page<Transaction> result = transactionService.search(searchDTO, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());

        result.getContent().forEach(transaction -> {
            assertEquals(222L, transaction.getCustomerId());
            assertEquals("8872838283", transaction.getAccountNo());
            assertTrue(transaction.getDescription().toUpperCase().contains("FUND TRANSFER"));
        });

        verify(transactionRepository, times(1))
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    public void GivenPagination_WhenSearch_ThenShouldReturnRequestedPage() {
        TransactionSearchDTO searchDTO = new TransactionSearchDTO();
        Pageable pageable = PageRequest.of(1, 2);

        List<Transaction> pageContent = Arrays.asList(transactions.get(2), transactions.get(3));
        Page<Transaction> expectedPage = new PageImpl<>(pageContent, pageable, 6);

        when(transactionRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(expectedPage);

        Page<Transaction> result = transactionService.search(searchDTO, pageable);

        assertNotNull(result);
        assertEquals(6, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getNumber());
        assertEquals(2, result.getSize());
        assertEquals(3, result.getTotalPages());

        verify(transactionRepository, times(1))
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    public void GivenNoMatchingCriteria_WhenSearch_ThenShouldReturnResult() {
        TransactionSearchDTO searchDTO = TransactionSearchDTO.builder()
                .customerId(999L)
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> expectedPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        when(transactionRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(expectedPage);

        Page<Transaction> result = transactionService.search(searchDTO, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(transactionRepository, times(1))
                .findAll(any(Specification.class), eq(pageable));
    }
}