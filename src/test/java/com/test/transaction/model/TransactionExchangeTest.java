package com.test.transaction.model;

import com.test.transaction.client.dto.ExchangeRateData;
import com.test.transaction.entity.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionExchangeTest {

    @Test
    void getConvertedAmount_shouldMultiplyAmountByExchangeRate() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("100.00"));

        ExchangeRateData exchangeRateData = new ExchangeRateData();
        exchangeRateData.setExchangeRate(new BigDecimal("1.25"));

        TransactionExchange transactionExchange = TransactionExchange.builder()
                .exchangeRateData(exchangeRateData)
                .transaction(transaction)
                .build();

        // Act
        BigDecimal result = transactionExchange.getConvertedAmount();

        // Assert
        assertThat(result).isEqualByComparingTo("125.00");
    }

    @Test
    void getConvertedAmount_shouldRoundToTwoDecimalPlaces() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("100.00"));

        ExchangeRateData exchangeRateData = new ExchangeRateData();
        exchangeRateData.setExchangeRate(new BigDecimal("1.23456"));

        TransactionExchange transactionExchange = TransactionExchange.builder()
                .transaction(transaction)
                .exchangeRateData(exchangeRateData)
                .build();

        // Act
        BigDecimal result = transactionExchange.getConvertedAmount();

        // Assert
        assertThat(result).isEqualByComparingTo("123.46");
        assertThat(result.scale()).isEqualTo(2);
    }

    @Test
    void getConvertedAmount_shouldHandleZeroAmount() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.ZERO);

        ExchangeRateData exchangeRateData = new ExchangeRateData();
        exchangeRateData.setExchangeRate(new BigDecimal("1.50"));

        TransactionExchange transactionExchange = TransactionExchange.builder()
                .transaction(transaction)
                .exchangeRateData(exchangeRateData)
                .build();

        // Act
        BigDecimal result = transactionExchange.getConvertedAmount();

        // Assert
        assertThat(result).isEqualByComparingTo("0.00");
    }

    @Test
    void getConvertedAmount_shouldHandleLargeNumbers() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("999999.99"));

        ExchangeRateData exchangeRateData = new ExchangeRateData();
        exchangeRateData.setExchangeRate(new BigDecimal("2.50"));

        TransactionExchange transactionExchange = TransactionExchange.builder()
                .transaction(transaction)
                .exchangeRateData(exchangeRateData)
                .build();

        // Act
        BigDecimal result = transactionExchange.getConvertedAmount();

        // Assert
        assertThat(result).isEqualByComparingTo("2499999.98");
    }
}