package com.cloudnative;

import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class FuncNotificacionActividadSospechoza {

    @FunctionName("FuncNotificacionActividadSospechoza")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req", 
                route = "FuncNotificacionActividadSospechoza",
                methods = {HttpMethod.POST}, 
                authLevel = AuthorizationLevel.ANONYMOUS) 
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("FuncNotificacionActividadSospechoza, Java HTTP trigger processed a request.");

        
        String body = request.getBody().orElse("");
                
        try {
            if(body.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Cuerpo de la solicitud vacio").build();
            }    

            String message = "Se ha detectado una actividad sospechoza e informado al usuario: " + body;
            context.getLogger().info(message);

            return request.createResponseBuilder(HttpStatus.OK).body(message).build();

        } catch (Exception ex) {
            context.getLogger().severe("FuncNotificacionActividadSospechoza, Error procesando solicitud");
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + body).build();
        }

        
    }
}
