package com.example.assignment.web;

import com.example.assignment.domains.Transaction;
import com.example.assignment.services.TransactionService;
import com.example.assignment.services.mapper.TransactionMapper;
import com.example.assignment.web.dto.TransactionResponseDTO;
import com.example.assignment.web.dto.TransactionSearchDTO;
import com.example.assignment.web.dto.TransactionUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @GetMapping
    public ResponseEntity<Page<TransactionResponseDTO>> search(
            TransactionSearchDTO searchDTO,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Transaction> transactions = transactionService.search(searchDTO, pageable);
        Page<TransactionResponseDTO> response = transactions.map(transactionMapper::toDTO);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> update(
            @PathVariable String id,
            @Valid @RequestBody TransactionUpdateDTO updateDTO) {

        Transaction updatedTransaction = transactionService.updateDescription(id, updateDTO.getDescription());

        return ResponseEntity.ok(transactionMapper.toDTO(updatedTransaction));
    }
}