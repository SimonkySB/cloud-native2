package com.cloudnative.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;


@Configuration
public class WebClientConfig {

  @Bean
  public WebClient.Builder webClientBuilder() {
    ExchangeFilterFunction authFilter = ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            ClientRequest authorizedRequest = ClientRequest.from(clientRequest)
                    .headers(headers -> headers.setBearerAuth(getCurrentToken()))
                    .build();
            return Mono.just(authorizedRequest);
        });

    return WebClient.builder()
    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    .filter(authFilter);
  }

  private String getCurrentToken() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    String jwtToken = (String) attributes.getAttribute("jwtToken", 0);
    return jwtToken;
  }
}
