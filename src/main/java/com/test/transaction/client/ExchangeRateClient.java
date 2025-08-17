package com.test.transaction.client;

import com.test.transaction.client.dto.ExchangeRateResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/v1/accounting/od/rates_of_exchange")
public interface ExchangeRateClient {

    @GetExchange
    ExchangeRateResponse getExchangeRates(
            @RequestParam("fields") String fields,
            @RequestParam("filter") String filter,
            @RequestParam("sort") String sort,
            @RequestParam("page[size]") Integer pageSize,
            @RequestParam("page[number]") Integer pageNumber
    );
}
