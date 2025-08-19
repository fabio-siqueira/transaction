package com.test.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.transaction.controller.dto.TransactionCreationRequest;
import com.test.transaction.entity.Transaction;
import com.test.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Para suporte a LocalDate
    }

    @Test
    void createTransaction_shouldReturnCreatedTransaction() throws Exception {
        // Arrange
        TransactionCreationRequest request = new TransactionCreationRequest();
        request.setAmount(new BigDecimal("150.00"));
        request.setDescription("Compra online");
        request.setTransactionDate(LocalDate.of(2024, 6, 15));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(UUID.randomUUID());
        savedTransaction.setAmount(request.getAmount());
        savedTransaction.setDescription(request.getDescription());
        savedTransaction.setTransactionDate(request.getTransactionDate());

        when(transactionService.create(any(Transaction.class))).thenReturn(savedTransaction);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        MvcResult result = resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(request.getAmount()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andReturn();

        // Assert
        String responseContent = result.getResponse().getContentAsString();
        Transaction responseTransaction = objectMapper.readValue(responseContent, Transaction.class);

        assertThat(responseTransaction).isNotNull();
        assertThat(responseTransaction.getId()).isEqualTo(savedTransaction.getId());
        assertThat(responseTransaction.getAmount()).isEqualByComparingTo(request.getAmount());
        assertThat(responseTransaction.getDescription()).isEqualTo(request.getDescription());
        assertThat(responseTransaction.getTransactionDate()).isEqualTo(request.getTransactionDate());

        verify(transactionService).create(any(Transaction.class));
    }

    @Test
    void createTransaction_withDescriptionWithMinLength_shouldReturnCreatedTransaction() throws Exception {
        // Arrange
        TransactionCreationRequest request = new TransactionCreationRequest();
        request.setAmount(new BigDecimal("150.00"));
        request.setDescription("a"); // Descrição com tamanho mínimo
        request.setTransactionDate(LocalDate.of(2024, 6, 15));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(UUID.randomUUID());
        savedTransaction.setAmount(request.getAmount());
        savedTransaction.setDescription(request.getDescription());
        savedTransaction.setTransactionDate(request.getTransactionDate());

        when(transactionService.create(any(Transaction.class))).thenReturn(savedTransaction);

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        // Verificando resposta
        String responseContent = result.getResponse().getContentAsString();
        Transaction responseTransaction = objectMapper.readValue(responseContent, Transaction.class);

        assertThat(responseTransaction.getDescription()).isEqualTo(request.getDescription());
    }

    @Test
    void createTransaction_withNullDescription_shouldReturnBadRequest() throws Exception {
        // Arrange
        TransactionCreationRequest request = new TransactionCreationRequest();
        request.setAmount(new BigDecimal("150.00"));
        request.setDescription(null); // Descrição nula
        request.setTransactionDate(LocalDate.of(2024, 6, 15));

        // Act
        ResultActions resultActions = mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));


        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed: description: Description must not be blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));

    }

    @Test
    void createTransaction_withEmptyDescription_shouldReturnBadRequest() throws Exception {
        // Arrange
        TransactionCreationRequest request = new TransactionCreationRequest();
        request.setAmount(new BigDecimal("150.00"));
        request.setDescription(""); // Descrição vazia
        request.setTransactionDate(LocalDate.of(2024, 6, 15));

        // Act & Assert
        ResultActions resultActions = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed: description: Description must not be blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createTransaction_withNegativeAmount_shouldReturnBadRequest() throws Exception {
        // Arrange
        TransactionCreationRequest request = new TransactionCreationRequest();
        request.setAmount(new BigDecimal("-10.00")); // Valor negativo
        request.setDescription("Test purchase");
        request.setTransactionDate(LocalDate.of(2024, 6, 15));

        // Act & Assert
        ResultActions resultActions = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed: amount: must be greater than or equal to 0.00"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createTransaction_withNullAmount_shouldReturnBadRequest() throws Exception {
        // Arrange
        TransactionCreationRequest request = new TransactionCreationRequest();
        request.setAmount(null); // Valor nulo
        request.setDescription("Compra teste");
        request.setTransactionDate(LocalDate.of(2024, 6, 15));

        // Act & Assert
        ResultActions resultActions = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed: amount: Amount must not be null"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createTransaction_withNullTransactionDate_shouldReturnBadRequest() throws Exception {
        // Arrange
        TransactionCreationRequest request = new TransactionCreationRequest();
        request.setAmount(new BigDecimal("150.00"));
        request.setDescription("Compra teste");
        request.setTransactionDate(null); // Data nula

        // Act & Assert
        ResultActions resultActions = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed: transactionDate: Transaction date must not be null"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createTransaction_withFutureDate_shouldReturnBadRequest() throws Exception {
        // Arrange
        TransactionCreationRequest request = new TransactionCreationRequest();
        request.setAmount(new BigDecimal("150.00"));
        request.setDescription("Compra teste");
        request.setTransactionDate(LocalDate.now().plusDays(1)); // Data futura

        // Act & Assert
        ResultActions resultActions = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed: transactionDate: must be a date in the past or in the present"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }
}