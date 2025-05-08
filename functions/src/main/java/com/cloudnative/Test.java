package com.cloudnative;


import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;


public class Test {
    
    @FunctionName("Test")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {


        context.getLogger().info("Java HTTP trigger processed a request.");

        try {
            WalletLoader.downloadAndUnzipWallet();
            String roles = new Dao().getAll();

            return request.createResponseBuilder(HttpStatus.OK).body(roles).build();
        } catch (Exception e) {
            context.getLogger().severe("Error: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }
}
