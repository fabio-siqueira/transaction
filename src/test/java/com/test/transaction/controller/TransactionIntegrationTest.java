package com.test.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.transaction.controller.dto.TransactionCreationRequest;
import com.test.transaction.entity.Transaction;
import com.test.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepository transactionRepository;


    @AfterEach
    void cleanup() {
        transactionRepository.deleteAll();
    }

    @Test
    void testCreateAndRetrieveTransactionWithExchangeRate() throws Exception {
        // Cria uma transação
        TransactionCreationRequest request = new TransactionCreationRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setDescription("Compra teste integração");
        request.setTransactionDate(LocalDate.of(2025, 8, 14));

        // Act - cria a transação via API
        MvcResult createResult = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert - verifica se a transação foi criada corretamente
        String createResponseContent = createResult.getResponse().getContentAsString();
        Transaction createdTransaction = objectMapper.readValue(createResponseContent, Transaction.class);

        assertThat(createdTransaction).isNotNull();
        assertThat(createdTransaction.getId()).isNotNull();
        assertThat(createdTransaction.getAmount()).isEqualByComparingTo(request.getAmount());
        assertThat(createdTransaction.getDescription()).isEqualTo(request.getDescription());
        assertThat(createdTransaction.getTransactionDate()).isEqualTo(request.getTransactionDate());

        // Verifica se a transação está no banco de dados
        UUID transactionId = createdTransaction.getId();
        assertThat(transactionRepository.findById(transactionId)).isPresent();

        // Act - consulta a conversão da moeda
        String targetCurrency = "Brazil-Real";
        ResultActions resultActions = mockMvc.perform(get("/api/transactions/{id}/exchange", transactionId)
                .param("targetCurrency", targetCurrency)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert - verifica a conversão da moeda
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.transaction.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.exchangeRateData.countryCurrencyDesc").value(targetCurrency))
                .andExpect(jsonPath("$.exchangeRateData.exchangeRate").isNotEmpty())
                .andExpect(jsonPath("$.convertedAmount").value("547.80"));
    }

    @Test
    void testExchangeRateNotAvailableWithin6Months() throws Exception {

        // Cria uma transação
        TransactionCreationRequest request = new TransactionCreationRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setDescription("Compra sem taxa");
        request.setTransactionDate(LocalDate.now().minusDays(5));

        // Act - cria a transação via API
        MvcResult createResult = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Transaction createdTransaction = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                Transaction.class
        );

        // Act & Assert - consulta a conversão da moeda deve falhar
        mockMvc.perform(get("/api/transactions/{id}/exchange", createdTransaction.getId())
                        .param("targetCurrency", "EUR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value("The purchase cannot be converted to the target currency: EUR"))
                .andExpect(jsonPath("$.title").value("Unprocessable Entity"))
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void testTransactionNotFound() throws Exception {
        // Act & Assert - consulta uma transação que não existe
        UUID nonExistentId = UUID.randomUUID();
        ResultActions resultActions = mockMvc.perform(get("/api/transactions/{id}/exchange", nonExistentId)
                .param("targetCurrency", "EUR")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Transaction not found with ID: " + nonExistentId));
    }
}