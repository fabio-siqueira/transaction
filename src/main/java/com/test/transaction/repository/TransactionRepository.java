package com.test.transaction.repository;

import com.test.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findAllByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
    List<Transaction> findAllByAmountGreaterThan(BigDecimal amount);
    List<Transaction> findByDescriptionContaining(String keyword);
}
