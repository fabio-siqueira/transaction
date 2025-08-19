package com.test.transaction.service;

import com.test.transaction.client.dto.ExchangeRateData;
import com.test.transaction.entity.Transaction;
import com.test.transaction.exception.NotFoundException;
import com.test.transaction.exception.UnprocessableEntityException;
import com.test.transaction.model.TransactionExchange;
import com.test.transaction.repository.ExchangeRateRepository;
import com.test.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionExchangeServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private TransactionExchangeService transactionExchangeService;

    private UUID transactionId;
    private Transaction transaction;
    private ExchangeRateData exchangeRateData;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();

        transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setTransactionDate(LocalDate.of(2024, 5, 15));
        transaction.setDescription("Test Transaction");

        exchangeRateData = new ExchangeRateData();
        exchangeRateData.setCountryCurrencyDesc("Canada-Dollar");
        exchangeRateData.setExchangeRate(new BigDecimal("1.25"));
        exchangeRateData.setRecordDate(LocalDate.of(2024, 5, 10));
    }

    @Test
    void getExchangeRateDecorator_shouldReturnCorrectTransactionExchange() {
        // Arrange
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(exchangeRateRepository.findMostRecentWithinSixMonthsOf("Canada-Dollar", transaction.getTransactionDate()))
                .thenReturn(Optional.of(exchangeRateData));

        // Act
        TransactionExchange result = transactionExchangeService.getExchangeRateDecorator(transactionId, "Canada-Dollar");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTransaction()).isEqualTo(transaction);
        assertThat(result.getExchangeRateData()).isEqualTo(exchangeRateData);
        assertThat(result.getConvertedAmount()).isEqualByComparingTo(new BigDecimal("125.00"));
    }

    @Test
    void getExchangeRateDecorator_shouldThrowNotFoundException_whenTransactionNotFound() {
        // Arrange
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> transactionExchangeService.getExchangeRateDecorator(transactionId, "Canada-Dollar"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Transaction not found with ID: " + transactionId);
    }

    @Test
    void getExchangeRateDecorator_shouldThrowUnprocessableEntityException_whenNoExchangeRateFound() {
        // Arrange
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(exchangeRateRepository.findMostRecentWithinSixMonthsOf("Mexico-Peso", transaction.getTransactionDate()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> transactionExchangeService.getExchangeRateDecorator(transactionId, "Mexico-Peso"))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessageContaining("The purchase cannot be converted to the target currency: Mexico-Peso");
    }

    @Test
    void getExchangeRateDecorator_shouldCalculateGetConvertedAmountCorrectly() {
        // Arrange
        transaction.setAmount(new BigDecimal("200.50"));
        exchangeRateData.setExchangeRate(new BigDecimal("0.75"));

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(exchangeRateRepository.findMostRecentWithinSixMonthsOf("Japan-Yen", transaction.getTransactionDate()))
                .thenReturn(Optional.of(exchangeRateData));

        // Act
        TransactionExchange result = transactionExchangeService.getExchangeRateDecorator(transactionId, "Japan-Yen");

        // Assert
        assertThat(result.getConvertedAmount()).isEqualByComparingTo(new BigDecimal("150.38"));
    }
}