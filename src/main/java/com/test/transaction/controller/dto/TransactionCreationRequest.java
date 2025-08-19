package com.test.transaction.controller.dto;

import com.test.transaction.entity.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Data
public class TransactionCreationRequest {

    @NotBlank(message = "Description must not be blank")
    @Size(max = 50, message = "Description must not exceed 50 characters")
    private String description;

    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd", fallbackPatterns = {"yyyy/MM/dd", "dd-MM-yyyy", "dd/MM/yyyy"})
    @NotNull(message = "Transaction date must not be null")
    private LocalDate transactionDate;

    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    public Transaction toTransaction() {
        Transaction transaction = new Transaction();
        transaction.setDescription(this.description);
        transaction.setTransactionDate(this.transactionDate);
        transaction.setAmount(this.amount.setScale(2, RoundingMode.HALF_UP));
        return transaction;
    }
}

