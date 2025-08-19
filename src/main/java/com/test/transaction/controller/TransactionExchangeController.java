package com.test.transaction.controller;

import com.test.transaction.controller.dto.TransactionExchangeResponse;
import com.test.transaction.model.TransactionExchange;
import com.test.transaction.service.TransactionExchangeService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/transactions/{id}/exchange")
public class TransactionExchangeController {

    private final TransactionExchangeService transactionExchangeService;

    public TransactionExchangeController(TransactionExchangeService transactionExchangeService) {
        this.transactionExchangeService = transactionExchangeService;
    }

    @GetMapping
    public TransactionExchangeResponse getTransactionExchange(@PathVariable UUID id, @RequestParam String targetCurrency) {
        TransactionExchange transactionExchange = transactionExchangeService.getTransactionExchange(id, targetCurrency);
        return TransactionExchangeResponse.from(transactionExchange);
    }
}
