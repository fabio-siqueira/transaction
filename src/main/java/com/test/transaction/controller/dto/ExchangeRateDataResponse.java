package com.test.transaction.controller.dto;


import com.test.transaction.client.dto.ExchangeRateData;

record ExchangeRateDataResponse(String countryCurrencyDesc, String exchangeRate, String recordDate) {

    public static ExchangeRateDataResponse from(ExchangeRateData exchangeRateData) {
        return new ExchangeRateDataResponse(
                exchangeRateData.getCountryCurrencyDesc(),
                exchangeRateData.getExchangeRate().toString(),
                exchangeRateData.getRecordDate().toString()
        );
    }
}
