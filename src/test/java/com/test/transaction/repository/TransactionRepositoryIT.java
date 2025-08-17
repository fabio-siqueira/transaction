package com.test.transaction.repository;

import com.test.transaction.TransactionApplicationTests;
import com.test.transaction.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TransactionRepositoryIT extends TransactionApplicationTests {

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();

        sampleTransaction = new Transaction();
        sampleTransaction.setDescription("Test purchase");
        sampleTransaction.setTransactionDate(LocalDate.now());
        sampleTransaction.setAmount(new BigDecimal("100.00"));

        transactionRepository.save(sampleTransaction);
    }

    @Test
    void saveTransactionSuccessfully() {
        Transaction transaction = new Transaction();
        transaction.setDescription("New purchase");
        LocalDate transactionDate = LocalDate.now();
        transaction.setTransactionDate(transactionDate);
        transaction.setAmount(new BigDecimal("250.50"));

        Transaction savedTransaction = transactionRepository.save(transaction);

        assertThat(savedTransaction.getId()).isNotNull();
        assertThat(savedTransaction.getDescription()).isEqualTo("New purchase");
        assertThat(savedTransaction.getTransactionDate()).isEqualTo(transactionDate);
        assertThat(savedTransaction.getAmount()).isEqualTo(new BigDecimal("250.50"));
    }

    @Test
    void findTransactionById() {
        Optional<Transaction> foundTransaction = transactionRepository.findById(sampleTransaction.getId());

        assertThat(foundTransaction).isPresent();
        assertThat(foundTransaction.get().getId()).isEqualTo(sampleTransaction.getId());
    }

    @Test
    void findTransactionByNonExistentIdReturnsEmpty() {
        Optional<Transaction> foundTransaction = transactionRepository.findById(UUID.randomUUID());
        assertThat(foundTransaction).isEmpty();
    }

    @Test
    void findAllTransactions() {
        Transaction secondTransaction = new Transaction();
        secondTransaction.setDescription("Second purchase");
        secondTransaction.setTransactionDate(LocalDate.now().minusDays(1));
        secondTransaction.setAmount(new BigDecimal("75.25"));
        transactionRepository.save(secondTransaction);

        List<Transaction> allTransactions = transactionRepository.findAll();

        assertThat(allTransactions.size()).isEqualTo(2);
    }

    @Test
    void deleteTransaction() {
        transactionRepository.delete(sampleTransaction);

        Optional<Transaction> deletedTransaction = transactionRepository.findById(sampleTransaction.getId());
        assertThat(deletedTransaction).isEmpty();
    }

    @Test
    void findTransactionsByDateRange() {
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(5);

        List<Transaction> transactions = transactionRepository.findAllByTransactionDateBetween(startDate, endDate);

        assertThat(transactions.size()).isEqualTo(1);
        assertThat(transactions.getFirst().getId()).isEqualTo(sampleTransaction.getId());
    }

    @Test
    void findTransactionsByAmountGreaterThan() {
        List<Transaction> transactions = transactionRepository.findAllByAmountGreaterThan(new BigDecimal("50.00"));

        assertThat(transactions.size()).isEqualTo(1);
        assertThat(transactions.getFirst().getId()).isEqualTo(sampleTransaction.getId());
    }

    @Test
    void findTransactionsByDescriptionContaining() {
        List<Transaction> transactions = transactionRepository.findByDescriptionContaining("Test");

        assertThat(transactions.size()).isEqualTo(1);
        assertThat(transactions.getFirst().getId()).isEqualTo(sampleTransaction.getId());
    }
}