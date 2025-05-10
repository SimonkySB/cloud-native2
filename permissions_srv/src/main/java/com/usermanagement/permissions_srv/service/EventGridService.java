package com.usermanagement.permissions_srv.service;



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

 

  public String ExecuteRolDeletedEventFor(Long rolId) throws IOException, InterruptedException {
    String body = rolId.toString();
    String eventType = "role_deleted";

    HttpResponse<String> response = Send(body, eventType);
    return response.body();
  }

  private HttpResponse<String> Send(String body, String eventType) throws IOException, InterruptedException {
    String eventId = UUID.randomUUID().toString();
    String timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);

    String dataJson = String.format("\"%s\"", body.replace("\"", "\\\""));

    String eventPayload = String.format(
        "[{\"id\":\"%s\"," +
            "\"eventType\":\"%s\"," +
            "\"subject\":\"/example/subject\"," +
            "\"eventTime\":\"%s\"," +
            "\"data\":%s," +
            "\"dataVersion\":\"1.0\"}]",
        eventId, eventType, timestamp, dataJson);

    System.out.println(String.format("Enviando evento: %s", eventPayload));

    HttpRequest httpRequest = HttpRequest.newBuilder()
        .uri(URI.create(eventGridTopicEndpoint))
        .header("Content-Type", "application/json")
        .header("aeg-sas-key", eventGridKey)
        .POST(HttpRequest.BodyPublishers.ofString(eventPayload))
        .build();

    HttpClient client = HttpClient.newHttpClient();

    return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
  }

  
}
