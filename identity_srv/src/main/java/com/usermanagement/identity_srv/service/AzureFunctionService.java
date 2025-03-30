package com.usermanagement.identity_srv.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AzureFunctionService {
    private RestTemplate restTemplate;

    @Value("${azure.function.suspicious-activity-url}")
    private String suspiciousActivityUrl;

    public AzureFunctionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String ExecuteSuspiciousActivityFor(String email) {
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> requestEntity = new HttpEntity<String>(email, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            suspiciousActivityUrl, HttpMethod.POST, requestEntity, String.class);

        return response.getBody();
    } 
}
