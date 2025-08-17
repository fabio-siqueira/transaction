package com.test.transaction.repository;

import com.test.transaction.client.ExchangeRateClient;
import com.test.transaction.client.dto.ExchangeRateData;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class ExchangeRateRepository {

    public static final String COUNTRY_CURRENCY_DESCRIPTION = "country_currency_desc";
    public static final String EXCHANGE_RATE = "exchange_rate";
    public static final String RECORD_DATE = "record_date";

    public static final String GREATER_THAN_OR_EQUAL_TO = "gte";
    public static final String LESS_THAN_OR_EQUAL_TO = "lte";
    public static final String EQUAL_TO = "eq";

    public static final String COLON = ":";
    public static final String COMMA = ",";

    private final ExchangeRateClient exchangeRateClient;

    public ExchangeRateRepository(ExchangeRateClient exchangeRateClient) {
        this.exchangeRateClient = exchangeRateClient;
    }

    public Optional<ExchangeRateData> findMostRecentWithinSixMonthsOf(String countryCurrency, LocalDate recordDate) {
        LocalDate startDate = recordDate.minusMonths(6);
        return findAllForCurrencyWithinDateRange(countryCurrency, startDate, recordDate, 1, 1)
                .stream()
                .findAny();
    }

    private List<ExchangeRateData> findAllForCurrencyWithinDateRange(String countryCurrency, LocalDate startDate, LocalDate endDate, Integer page, Integer size) {

        StringBuilder filter = new StringBuilder();
        appendFilter(COUNTRY_CURRENCY_DESCRIPTION, EQUAL_TO, countryCurrency, filter).append(COMMA);
        appendFilter(RECORD_DATE, GREATER_THAN_OR_EQUAL_TO, startDate.toString(), filter).append(COMMA);
        appendFilter(RECORD_DATE, LESS_THAN_OR_EQUAL_TO, endDate.toString(), filter);

        String fields = String.join(COMMA, COUNTRY_CURRENCY_DESCRIPTION, EXCHANGE_RATE, RECORD_DATE);
        String sort = "-" + RECORD_DATE; // Sort by record_date in descending order
        return exchangeRateClient.getExchangeRates(fields, filter.toString(), sort, size, page).getData();
    }

    private StringBuilder appendFilter(String field, String operator, String value, StringBuilder filter) {
        return filter.append(field).append(COLON).append(operator).append(COLON).append(value);
    }
}
