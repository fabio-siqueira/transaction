package com.test.transaction.config;

import com.test.transaction.client.ExchangeRateClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {

    @Value("${api.exchange-rate.base-url}")
    private String exchangeRateApiBaseUrl;


    @Bean
    public ExchangeRateClient exchangeRateClient() {

        RestClient restClient = RestClient.builder()
                .baseUrl(exchangeRateApiBaseUrl)
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return factory.createClient(ExchangeRateClient.class);
    }

}
