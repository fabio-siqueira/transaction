package com.test.transaction.service;

import com.test.transaction.client.dto.ExchangeRateData;
import com.test.transaction.entity.Transaction;
import com.test.transaction.exception.NotFoundException;
import com.test.transaction.exception.UnprocessableEntityException;
import com.test.transaction.model.TransactionExchange;
import com.test.transaction.repository.ExchangeRateRepository;
import com.test.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionExchangeService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final TransactionRepository transactionRepository;

    public TransactionExchangeService(TransactionRepository transactionRepository, ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.transactionRepository = transactionRepository;
    }

    public TransactionExchange getTransactionExchange(UUID transactionId, String countryCurrency) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found with ID: " + transactionId));

        ExchangeRateData exchangeRateData = exchangeRateRepository.findMostRecentWithinSixMonthsOf(countryCurrency, transaction.getTransactionDate())
                .orElseThrow(() -> new UnprocessableEntityException("The purchase cannot be converted to the target currency: " + countryCurrency));

        return TransactionExchange.builder()
                .transaction(transaction)
                .exchangeRateData(exchangeRateData)
                .build();
    }
}
