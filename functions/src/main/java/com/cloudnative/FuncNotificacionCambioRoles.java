package com.cloudnative;

import java.util.*;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class FuncNotificacionCambioRoles {
    
    @FunctionName("FuncNotificacionCambioRoles")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req", 
                route = "FuncNotificacionCambioRoles",
                methods = {HttpMethod.POST}, 
                authLevel = AuthorizationLevel.ANONYMOUS) 
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {


        context.getLogger().info("FuncNotificacionCambioRoles, Java HTTP trigger processed a request.");

        
        String body = request.getBody().orElse("");
        
        try {
            if(body.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Cuerpo de la solicitud vacio").build();
            }    

            String message = body;
            context.getLogger().info(message);
            return request.createResponseBuilder(HttpStatus.OK).body(message).build();

        } catch (Exception ex) {
            context.getLogger().severe("FuncNotificacionCambioRoles, Error procesando solicitud");
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + body).build();
        }
        

        
        
    }
}
