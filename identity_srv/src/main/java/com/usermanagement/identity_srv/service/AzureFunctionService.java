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

    @Value("${azure.function.rol-change-url}")
    private String rolChangeUrl;

    public AzureFunctionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String ExecuteRolChangeFor(String email, String roleName) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String message = "Role has been update to " + roleName + " for user: " + email;
        HttpEntity<String> requestEntity = new HttpEntity<String>(message, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            rolChangeUrl, HttpMethod.POST, requestEntity, String.class);

        return response.getBody();
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
