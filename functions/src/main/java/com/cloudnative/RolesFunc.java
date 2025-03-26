package com.cloudnative;

import java.util.*;


import com.microsoft.azure.functions.annotation.*;
import com.cloudnative.dao.RolDao;
import com.cloudnative.models.Rol;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class RolesFunc {
    
    private RolDao rolDao = new RolDao();

    /**
     * This function listens at endpoint "/api/RolesFunc". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/RolesFunc
     * 2. curl {your host}/api/RolesFunc?name=HTTP%20Query
     */
    @FunctionName("RolesFunc")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req", 
                route = "roles/{strId=EMPTY}",
                methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE}, 
                authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context, @BindingName("strId") String strId) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String body = request.getBody().orElse("");
    
        Long id = null;
        try {
            id = (strId == null || strId.contains("EMPTY") || strId.isEmpty()) ? null : Long.valueOf(strId);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        switch (request.getHttpMethod()) {
            case GET:
                if(id != null) {
                    Rol rol = rolDao.getById(id);
                    if(rol != null) {
                        return request.createResponseBuilder(HttpStatus.OK).body(rol).build();
                    }
                    return request.createResponseBuilder(HttpStatus.NOT_FOUND).build();
                } else {
                    List<Rol> roles = rolDao.getAll();
                    return request.createResponseBuilder(HttpStatus.OK).body(roles).build();
                }
                
            case POST:
                try {
                    Rol role = mapper.readValue(body, Rol.class);
                    rolDao.create(role);
                    return request.createResponseBuilder(HttpStatus.CREATED).build();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build();
                } 
            case PUT:
                if(id == null) {
                    return request.createResponseBuilder(HttpStatus.NOT_FOUND).build();
                }
                try {
                    Rol role = mapper.readValue(body, Rol.class);
                    role.setId(id);
                    rolDao.update(role);
                    return request.createResponseBuilder(HttpStatus.OK).build();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build();
                } 
            case DELETE:
                if(id == null) {
                    return request.createResponseBuilder(HttpStatus.NOT_FOUND).build();
                } 
                rolDao.delete(id);
                return request.createResponseBuilder(HttpStatus.NO_CONTENT).build();
            default:
                return request.createResponseBuilder(HttpStatus.METHOD_NOT_ALLOWED).body("Method not allowed").build();
        }
    }
}
