package com.cloudnative.bff.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bff/http")
public class HttpController {

  @Autowired
  private WebClient.Builder webClientBuilder;

  private final String BASE_URL = "https://az-functions-cloud.azurewebsites.net/api";

  @GetMapping("/login-attempts")
  public Mono<ResponseEntity<String>> activeSessions(@RequestParam String username) {
    return webClientBuilder
        .baseUrl(BASE_URL)
        .build()
        .get()
        .uri(builder -> builder
            .path("/login-attempts")
            .queryParam("username", username)
            .build()
        )
        .retrieve()
        .toEntity(String.class);
  }

  @PostMapping("/password-reset")
  public Mono<ResponseEntity<String>> roleaudit(@RequestBody String body) {
    return webClientBuilder
        .baseUrl(BASE_URL)
        .build()    
        .post()
        .uri("/password-reset")
        .bodyValue(body)
        .retrieve()
        .toEntity(String.class);
  }
}
