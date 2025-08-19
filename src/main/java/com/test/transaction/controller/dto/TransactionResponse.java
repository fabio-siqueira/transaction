package com.test.transaction.controller.dto;

import com.test.transaction.entity.Transaction;

public record TransactionResponse(
        String id,
        String description,
        String amount,
        String transactionDate
) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId().toString(),
                transaction.getDescription(),
                transaction.getAmount().toString(),
                transaction.getTransactionDate().toString()
        );
    }
}
