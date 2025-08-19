package com.test.transaction.controller.dto;

import com.test.transaction.model.TransactionExchange;

public record TransactionExchangeResponse(
    TransactionResponse transaction,
    ExchangeRateDataResponse exchangeRateData,
    String convertedAmount
) {
    public static TransactionExchangeResponse from(TransactionExchange transactionExchange) {
        return new TransactionExchangeResponse(
            TransactionResponse.from(transactionExchange.getTransaction()),
            ExchangeRateDataResponse.from(transactionExchange.getExchangeRateData()),
            transactionExchange.getConvertedAmount().toString()
        );
    }
}
