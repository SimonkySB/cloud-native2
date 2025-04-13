package com.cloudnative;

import java.util.*;
import java.util.stream.Collectors;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class LoginAttemptFunction {
    /**
     * This function listens at endpoint "/api/LoginAttemptFunction". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/LoginAttemptFunction
     * 2. curl {your host}/api/LoginAttemptFunction?name=HTTP%20Query
     */
    @FunctionName("LoginAttemptFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS, route = "login-attempts") HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {

            context.getLogger().info("LoginAttemptFunction: Java HTTP trigger processed a request.");

            // Parse query parameter
            String username = request.getQueryParameters().get("username");
            

            if (username == null || username.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Please pass a 'username' on the query string").build();
            }

            List<LoginAttempt> filtered = loginAttempts.stream()
                    .filter(a -> a.username.equalsIgnoreCase(username))
                    .collect(Collectors.toList());

        
        return request.createResponseBuilder(HttpStatus.OK).body(filtered).build();
        } catch (Exception e) {
            context.getLogger().severe("❌ Error ejecutando LoginAttemptFunction: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage())
                .build();
          }
        

    }



    

    private List<LoginAttempt> loginAttempts = Arrays.asList(
        new LoginAttempt("alice", "2025-04-10T08:00:00", "192.168.1.10", "Mozilla/5.0 (Windows NT 10.0)", "device-001"),
        new LoginAttempt("bob", "2025-04-10T09:00:00", "192.168.1.11", "Mozilla/5.0 (Macintosh)", "device-002"),
        new LoginAttempt("alice", "2025-04-11T10:30:00", "192.168.1.12", "Mozilla/5.0 (Android)", "device-003"),
        new LoginAttempt("charlie", "2025-04-12T11:15:00", "192.168.1.13", "Mozilla/5.0 (iPhone)", "device-004"),
        new LoginAttempt("bob", "2025-04-13T12:45:00", "192.168.1.14", "Mozilla/5.0 (Linux)", "device-005")
    );


    public static class LoginAttempt {
        public String username;
        public String timestamp;
        public String ip;
        public String userAgent;
        public String deviceId;
    
        public LoginAttempt(String username, String timestamp, String ip, String userAgent, String deviceId) {
            this.username = username;
            this.timestamp = timestamp;
            this.ip = ip;
            this.userAgent = userAgent;
            this.deviceId = deviceId;
        }
    }
    
}


