package com.usermanagement.identity_srv.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class EventGridService {
    

    @Value("${event-grid.key}")
    private String eventGridKey;

    @Value("${event-grid.topic-endpoint}")
    private String eventGridTopicEndpoint;


    public String ExecuteRolChangeFor(String email, String roleName) throws IOException, InterruptedException {
        
        String body = "Role has been update to " + roleName + " for user: " + email;
        String eventType = "role_change";

        String eventId = UUID.randomUUID().toString();
        String timestamp = ZonedDateTime.now(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT);

        String eventPayload = String.format(
          "[{\"id\":\"%s\"," +
              "\"eventType\":\"%s\"," +
              "\"subject\":\"/example/subject\"," +
              "\"eventTime\":\"%s\"," +
              "\"data\":%s," +
              "\"dataVersion\":\"1.0\"}]",
          eventId, eventType, timestamp, body);

        
        System.out.println(String.format("Enviando evento: %s", eventPayload));

        HttpRequest httpRequest = HttpRequest.newBuilder()
          .uri(URI.create(eventGridTopicEndpoint))
          .header("Content-Type", "application/json")
          .header("aeg-sas-key", eventGridKey)
          .POST(HttpRequest.BodyPublishers.ofString(eventPayload))
          .build();

        HttpClient client = HttpClient.newHttpClient();
        
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String ExecuteSuspiciousActivityFor(String email) throws IOException, InterruptedException {
        String body = email;
        String eventType = "suspicious_activity";

        String eventId = UUID.randomUUID().toString();
        String timestamp = ZonedDateTime.now(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT);

        String eventPayload = String.format(
          "[{\"id\":\"%s\"," +
              "\"eventType\":\"%s\"," +
              "\"subject\":\"/example/subject\"," +
              "\"eventTime\":\"%s\"," +
              "\"data\":%s," +
              "\"dataVersion\":\"1.0\"}]",
          eventId, eventType, timestamp, body);

        
        System.out.println(String.format("Enviando evento: %s", eventPayload));

        HttpRequest httpRequest = HttpRequest.newBuilder()
          .uri(URI.create(eventGridTopicEndpoint))
          .header("Content-Type", "application/json")
          .header("aeg-sas-key", eventGridKey)
          .POST(HttpRequest.BodyPublishers.ofString(eventPayload))
          .build();

        HttpClient client = HttpClient.newHttpClient();
        
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
