package com.test.transaction.repository;

import com.test.transaction.client.ExchangeRateClient;
import com.test.transaction.client.dto.ExchangeRateData;
import com.test.transaction.client.dto.ExchangeRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateRepositoryTest {

    @Mock
    private ExchangeRateClient exchangeRateClient;

    @InjectMocks
    private ExchangeRateRepository exchangeRateRepository;

    private ExchangeRateResponse responseWithData;
    private ExchangeRateResponse emptyResponse;

    @BeforeEach
    void setUp() {
        ExchangeRateData exchangeRateData = new ExchangeRateData();
        exchangeRateData.setCountryCurrencyDesc("Canada-Dollar");
        exchangeRateData.setExchangeRate(new BigDecimal("1.25"));
        exchangeRateData.setRecordDate(LocalDate.of(2024, 5, 15));

        responseWithData = new ExchangeRateResponse();
        responseWithData.setData(List.of(exchangeRateData));

        emptyResponse = new ExchangeRateResponse();
        emptyResponse.setData(new ArrayList<>());
    }

    @Test
    void findMostRecentWithinSixMonthsOf_shouldReturnMostRecentRate_whenRateExists() {
        // Arrange
        LocalDate recordDate = LocalDate.of(2024, 6, 1);
        LocalDate sixMonthsAgo = recordDate.minusMonths(6);

        String expectedFields = "country_currency_desc,exchange_rate,record_date";
        String expectedFilterPart = "country_currency_desc:eq:Canada-Dollar,record_date:gte:" + sixMonthsAgo;

        when(exchangeRateClient.getExchangeRates(
                eq(expectedFields),
                contains(expectedFilterPart),
                anyString(),
                eq(1),
                eq(1)
        )).thenReturn(responseWithData);

        // Act
        Optional<ExchangeRateData> result = exchangeRateRepository.findMostRecentWithinSixMonthsOf("Canada-Dollar", recordDate);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getCountryCurrencyDesc()).isEqualTo("Canada-Dollar");
        assertThat(result.get().getExchangeRate()).isEqualByComparingTo("1.25");
        assertThat(result.get().getRecordDate()).isEqualTo(LocalDate.of(2024, 5, 15));
    }

    @Test
    void findMostRecentWithinSixMonthsOf_shouldReturnEmpty_whenNoRateExists() {
        // Arrange
        LocalDate recordDate = LocalDate.of(2024, 6, 1);

        when(exchangeRateClient.getExchangeRates(
                anyString(),
                anyString(),
                anyString(),
                anyInt(),
                anyInt()
        )).thenReturn(emptyResponse);

        // Act
        Optional<ExchangeRateData> result = exchangeRateRepository.findMostRecentWithinSixMonthsOf("Japan-Yen", recordDate);

        // Assert
        assertThat(result).isEmpty();
    }
}