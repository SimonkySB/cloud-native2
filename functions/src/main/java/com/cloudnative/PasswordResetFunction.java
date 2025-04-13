package com.cloudnative;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class PasswordResetFunction {
    /**
     * This function listens at endpoint "/api/PasswordResetFunction". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/PasswordResetFunction
     * 2. curl {your host}/api/PasswordResetFunction?name=HTTP%20Query
     */
    @FunctionName("PasswordResetFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            context.getLogger().info("PasswordResetFunction: Java HTTP trigger processed a request.");

            
            String body = request.getBody().orElse("");
            ObjectMapper mapper = new ObjectMapper();
            PasswordResetRequest data = mapper.readValue(body, PasswordResetRequest.class);
    
            if (data.email == null || data.email.trim().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("The email field is missing in the request body.").build();
            }

            if (!mockEmailExists(data.email)) {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("The email is not registered: " + data.email).build();
            }

            String resetLink = "https://cloudnative.com/reset-password?token=abc123&email=" + data.email;

            PasswordResetResponse response = new PasswordResetResponse("success",
                "The reset link has been sent to " + data.email,
                resetLink);

            return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response).build();

        } catch(Exception e) {
            context.getLogger().severe("❌ Error ejecutando PasswordResetFunction: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage())
                .build();
        }
        
    }

    private boolean mockEmailExists(String email) {
        return email.equalsIgnoreCase("alice@example.com") ||
               email.equalsIgnoreCase("bob@example.com") ||
               email.equalsIgnoreCase("charlie@example.com");
    }

    public static class PasswordResetRequest {
        public String email;
    }

    public static class PasswordResetResponse {
        public String status;
        public String message;
        public String resetLink;

        public PasswordResetResponse(String status, String message, String resetLink) {
            this.status = status;
            this.message = message;
            this.resetLink = resetLink;
        }
    }
}
