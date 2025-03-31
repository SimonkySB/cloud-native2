package com.cloudnative.bff.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/bff/users")
public class UsuarioController {
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${identity-url}")
    private String BASE_URL;

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody String body) {
        return webClientBuilder.build()
                .post()
                .uri(BASE_URL + "/login")
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class);
      }

    @GetMapping
    public Mono<ResponseEntity<String>> findAll() {
        return webClientBuilder.build()
                .get()
                .uri(BASE_URL)
                .retrieve()
                .toEntity(String.class);
    }

    // @GetMapping("/{id}")
    // public Mono<ResponseEntity<String>> findById(@PathVariable String id) {
    //     return webClientBuilder.build()
    //             .get()
    //             .uri(BASE_URL + "/" + id)
    //             .retrieve()
    //             .toEntity(String.class);
    // }

    @PostMapping
    public Mono<ResponseEntity<String>> create(@RequestBody String body) {
        return webClientBuilder.build()
                .post()
                .uri(BASE_URL)
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class);
    }

    @PutMapping("/{id}/status")
    public Mono<ResponseEntity<String>> updateUserStatus(@PathVariable String id, @RequestBody String body) {
        return webClientBuilder.build()
                .put()
                .uri(BASE_URL + "/" + id + "/status")
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<String>> update(@PathVariable String id, @RequestBody String body) {
        return webClientBuilder.build()
                .put()
                .uri(BASE_URL + "/" + id)
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class);
    }


    @GetMapping("/me")
    public Mono<ResponseEntity<String>> me() {
        return webClientBuilder.build()
                .get()
                .uri(BASE_URL + "/me")
                .retrieve()
                .toEntity(String.class);
    }

    // @DeleteMapping("/{id}")
    // public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
    //     return webClientBuilder.build()
    //             .delete()
    //             .uri(BASE_URL + "/" + id)
    //             .retrieve()
    //             .toEntity(String.class);
    // }
}
