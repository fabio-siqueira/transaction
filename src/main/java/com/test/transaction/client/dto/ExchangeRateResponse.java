package com.test.transaction.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ExchangeRateResponse {
    private List<ExchangeRateData> data;
    private Meta meta;
}
