package com.test.transaction.model;

import com.test.transaction.client.dto.ExchangeRateData;
import com.test.transaction.entity.Transaction;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Builder
public class TransactionExchange {
    private Transaction transaction;
    private ExchangeRateData exchangeRateData;

    public BigDecimal getConvertedAmount() {
        return transaction.getAmount()
                .multiply(exchangeRateData.getExchangeRate())
                .setScale(2, RoundingMode.HALF_UP);
    }
}