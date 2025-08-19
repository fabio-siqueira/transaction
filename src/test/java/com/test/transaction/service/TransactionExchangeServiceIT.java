package com.test.transaction.service;

import com.test.transaction.TransactionApplicationTests;
import com.test.transaction.entity.Transaction;
import com.test.transaction.repository.TransactionRepository;
import com.test.transaction.model.TransactionExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionExchangeServiceIT extends TransactionApplicationTests {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionExchangeService transactionExchangeService;

    Transaction savedTransaction;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        // Additional setup if needed

        Transaction transaction = new Transaction();
        transaction.setDescription("New purchase");
        LocalDate transactionDate = LocalDate.now();
        transaction.setTransactionDate(transactionDate);
        transaction.setAmount(new BigDecimal("250.53"));

        savedTransaction = transactionRepository.save(transaction);
    }

    @Test
    void testGetExchangeRateDecorator() {
        String testCurrency = "Brazil-Real";
        TransactionExchange response = transactionExchangeService.getExchangeRateDecorator(savedTransaction.getId(), testCurrency);

        assertThat(response).isNotNull();
        assertThat(response.getTransaction().getId()).isEqualTo(savedTransaction.getId());
        assertThat(response.getTransaction().getDescription()).isEqualTo(savedTransaction.getDescription());
        assertThat(response.getTransaction().getTransactionDate()).isEqualTo(savedTransaction.getTransactionDate());
        assertThat(response.getTransaction().getAmount()).isEqualTo(savedTransaction.getAmount());
        assertThat(response.getExchangeRateData()).isNotNull();
        assertThat(response.getExchangeRateData().getCountryCurrencyDesc()).isEqualTo(testCurrency);
        assertThat(response.getExchangeRateData().getExchangeRate()).isNotNull();
        assertThat(response.getExchangeRateData().getExchangeRate()).isGreaterThan(BigDecimal.ZERO);
        assertThat(response.getExchangeRateData().getRecordDate()).isNotNull();
        assertThat(response.getExchangeRateData().getRecordDate()).isBeforeOrEqualTo(savedTransaction.getTransactionDate());
        assertThat(response.getExchangeRateData().getRecordDate()).isAfterOrEqualTo(savedTransaction.getTransactionDate().minusMonths(6));
        assertThat(response.getConvertedAmount()).isNotNull();
        assertThat(response.getConvertedAmount()).isGreaterThan(BigDecimal.ZERO);
    }
}