package com.test.transaction.controller;

import com.test.transaction.client.dto.ExchangeRateData;
import com.test.transaction.entity.Transaction;
import com.test.transaction.exception.NotFoundException;
import com.test.transaction.exception.UnprocessableEntityException;
import com.test.transaction.model.TransactionExchange;
import com.test.transaction.service.TransactionExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TransactionExchangeController.class)
class TransactionExchangeControllerTest {

    @MockitoBean
    private TransactionExchangeService transactionExchangeService;

    @Autowired
    private MockMvc mockMvc;
    private UUID transactionId;
    private Transaction transaction;
    private TransactionExchange transactionExchange;

    @BeforeEach
    void setUp() {

        transactionId = UUID.randomUUID();

        transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDescription("Test Transaction");
        transaction.setTransactionDate(LocalDate.now());

        ExchangeRateData exchangeRateData = new ExchangeRateData();
        exchangeRateData.setExchangeRate(new BigDecimal("5.55"));
        exchangeRateData.setCountryCurrencyDesc("Brazil-Real");
        exchangeRateData.setRecordDate(LocalDate.now());

        transactionExchange = TransactionExchange.builder()
                .exchangeRateData(exchangeRateData)
                .transaction(transaction)
                .build();
    }

    @Test
    void getTransactionExchange_shouldReturnTransactionExchange() throws Exception {
        // Arrange
        String targetCurrency = "Brazil-Real";

        when(transactionExchangeService.getTransactionExchange(transactionId, targetCurrency))
                .thenReturn(transactionExchange);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/api/transactions/{id}/exchange", transactionId)
                .param("targetCurrency", targetCurrency)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.transaction.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.transaction.amount").value(100.00))
                .andExpect(jsonPath("$.transaction.description").value("Test Transaction"))
                .andExpect(jsonPath("$.transaction.transactionDate").value(transaction.getTransactionDate().toString()))
                .andExpect(jsonPath("$.exchangeRateData.exchangeRate").value(5.55))
                .andExpect(jsonPath("$.exchangeRateData.countryCurrencyDesc").value("Brazil-Real"))
                .andExpect(jsonPath("$.exchangeRateData.recordDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.convertedAmount").value(555.00));

        verify(transactionExchangeService, times(1)).getTransactionExchange(transactionId, targetCurrency);
    }

    @Test
    void getTransactionExchange_withInvalidCurrency_shouldHandleErrorCorrectly() throws Exception {
        // Arrange
        String invalidCurrency = "INVALID";
        String errorMessage = "Invalid target currency: " + invalidCurrency;

        when(transactionExchangeService.getTransactionExchange(transactionId, invalidCurrency))
                .thenThrow(new UnprocessableEntityException(errorMessage));

        // Act & Assert
        ResultActions resultActions = mockMvc.perform(get("/api/transactions/{id}/exchange", transactionId)
                .param("targetCurrency", invalidCurrency)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Unprocessable Entity"))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.detail").value(errorMessage));
    }

    @Test
    void getTransactionExchange_withNonExistingTransaction_shouldHandleErrorCorrectly() throws Exception {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        String targetCurrency = "EUR";
        String errorMessage = "Transação não encontrada com ID: " + nonExistingId;

        when(transactionExchangeService.getTransactionExchange(nonExistingId, targetCurrency))
                .thenThrow(new NotFoundException(errorMessage));

        // Act & Assert
        ResultActions resultActions = mockMvc.perform(get("/api/transactions/{id}/exchange", nonExistingId)
                        .param("targetCurrency", targetCurrency)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        resultActions.andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value(errorMessage));
    }

    @Test
    void getTransactionExchange_withMissingTargetCurrency_shouldHandleErrorCorrectly() throws Exception {
        // Act & Assert
        ResultActions resultActions = mockMvc.perform(get("/api/transactions/{id}/exchange", transactionId)
                        // Omitindo o parâmetro targetCurrency
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        resultActions.andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Required parameter 'targetCurrency' is not present."));
    }
}