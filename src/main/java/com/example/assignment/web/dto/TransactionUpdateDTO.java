package com.example.assignment.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionUpdateDTO {
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}