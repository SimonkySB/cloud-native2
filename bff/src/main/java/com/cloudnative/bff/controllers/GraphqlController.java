package com.cloudnative.bff.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bff/graphql")
public class GraphqlController {
  @Autowired
  private WebClient.Builder webClientBuilder;

  private final String BASE_URL = "https://az-functions-cloud.azurewebsites.net/api";

  @PostMapping("/active-sessions")
  public Mono<ResponseEntity<String>> activeSessions(@RequestBody String body) {
    return webClientBuilder.build()
        .post()
        .uri(BASE_URL + "/active-sessions")
        .bodyValue(body)
        .retrieve()
        .toEntity(String.class);
  }

  @PostMapping("/role-audit")
  public Mono<ResponseEntity<String>> roleaudit(@RequestBody String body) {
    return webClientBuilder.build()
        .post()
        .uri(BASE_URL + "/role-audit")
        .bodyValue(body)
        .retrieve()
        .toEntity(String.class);
  }
}
