package com.test.transaction.service;

import com.test.transaction.entity.Transaction;
import com.test.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    private Transaction transactionToCreate;
    private Transaction savedTransaction;

    @BeforeEach
    void setUp() {
        transactionToCreate = new Transaction();
        transactionToCreate.setAmount(new BigDecimal("150.75"));
        transactionToCreate.setDescription("Compra de material de escritório");
        transactionToCreate.setTransactionDate(LocalDate.of(2024, 5, 15));

        savedTransaction = new Transaction();
        savedTransaction.setId(UUID.randomUUID());
        savedTransaction.setAmount(transactionToCreate.getAmount());
        savedTransaction.setDescription(transactionToCreate.getDescription());
        savedTransaction.setTransactionDate(transactionToCreate.getTransactionDate());
    }

    @Test
    void create_shouldSaveAndReturnTransaction() {
        // Arrange
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // Act
        Transaction result = transactionService.create(transactionToCreate);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedTransaction.getId());
        assertThat(result.getAmount()).isEqualByComparingTo(savedTransaction.getAmount());
        assertThat(result.getDescription()).isEqualTo(savedTransaction.getDescription());
        assertThat(result.getTransactionDate()).isEqualTo(savedTransaction.getTransactionDate());

        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction capturedTransaction = transactionCaptor.getValue();
        assertThat(capturedTransaction.getAmount()).isEqualByComparingTo(transactionToCreate.getAmount());
        assertThat(capturedTransaction.getDescription()).isEqualTo(transactionToCreate.getDescription());
        assertThat(capturedTransaction.getTransactionDate()).isEqualTo(transactionToCreate.getTransactionDate());
    }
}