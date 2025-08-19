package com.test.transaction.controller;

import com.test.transaction.controller.dto.TransactionCreationRequest;
import com.test.transaction.controller.dto.TransactionResponse;
import com.test.transaction.entity.Transaction;
import com.test.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@Valid @RequestBody TransactionCreationRequest transactionCreationRequest) {
        Transaction transaction = transactionService.create(transactionCreationRequest.toTransaction());
        return TransactionResponse.from(transaction);
    }
}
