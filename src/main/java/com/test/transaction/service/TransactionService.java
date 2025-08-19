package com.test.transaction.service;

import com.test.transaction.entity.Transaction;
import com.test.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction create(Transaction transaction) {
        logger.info("Creating transaction {}", transaction);
        return transactionRepository.save(transaction);
    }
}
