package com.test.transaction.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id // Unique identifier: must uniquely identify the purchase
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Description: must not exceed 50 characters
    @Size(max = 50, message = "Description must not exceed 50 characters")
    private String description;

    // ● Transaction date: must be a valid date format
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Transaction date must not be null")
    private LocalDate transactionDate;

    //● Purchase amount: must be a valid positive amount rounded to the nearest cent
    @Min(value = 0, message = "Amount must be a positive value")
    private BigDecimal amount;
}
