package com.mx.lv.ordermanagment.configuration.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class HttpMockApiClientConfiguration {

    @Bean
    public RestClient mockApiRestClient(
            RestClient.Builder builder,
            @Value("${clients.mock-api.base-url}") String baseUrl
    ) {
        return builder
                .baseUrl(baseUrl)
                .build();
    }
}
