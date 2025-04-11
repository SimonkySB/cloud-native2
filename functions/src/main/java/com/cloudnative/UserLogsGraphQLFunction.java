package com.cloudnative;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import static graphql.Scalars.GraphQLString;
import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLList.list;
import graphql.schema.GraphQLObjectType;
import static graphql.schema.GraphQLObjectType.newObject;
import graphql.schema.GraphQLSchema;

/**
 * Azure Function con GraphQL para simular logs de usuarios.
 */
public class UserLogsGraphQLFunction {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final GraphQL graphQL;

  public UserLogsGraphQLFunction() {
    // Tipo para los detalles de acción
    GraphQLObjectType detailType = newObject()
        .name("ActionDetail")
        .field(newFieldDefinition().name("field").type(GraphQLString))
        .field(newFieldDefinition().name("oldValue").type(GraphQLString))
        .field(newFieldDefinition().name("newValue").type(GraphQLString))
        .build();

    // Tipo para la acción
    GraphQLObjectType actionType = newObject()
        .name("UserAction")
        .field(newFieldDefinition().name("action").type(GraphQLString))
        .field(newFieldDefinition().name("timestamp").type(GraphQLString))
        .field(newFieldDefinition().name("entity").type(GraphQLString))
        .field(newFieldDefinition().name("details").type(list(detailType)))
        .build();

    // Tipo para el usuario
    GraphQLObjectType userType = newObject()
        .name("UserLog")
        .field(newFieldDefinition().name("name").type(GraphQLString))
        .field(newFieldDefinition().name("email").type(GraphQLString))
        .field(newFieldDefinition().name("actions").type(list(actionType)))
        .build();

    // Query root
    GraphQLObjectType queryType = newObject()
        .name("Query")
        .field(newFieldDefinition()
            .name("getUserLogs")
            .type(list(userType)))
        .build();

    // DataFetcher con data de prueba
    GraphQLCodeRegistry codeRegistry = GraphQLCodeRegistry.newCodeRegistry()
        .dataFetcher(
            FieldCoordinates.coordinates("Query", "getUserLogs"),
            (DataFetcher<?>) env -> generateMockLogs() // ← casteo explícito
        )
        .build();

    GraphQLSchema schema = GraphQLSchema.newSchema()
        .query(queryType)
        .codeRegistry(codeRegistry)
        .build();

    this.graphQL = GraphQL.newGraphQL(schema).build();
  }

  @FunctionName("UserLogsGraphQLFunction")
  public HttpResponseMessage run(
      @HttpTrigger(name = "req", methods = {
          HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS, route = "user-logs") HttpRequestMessage<Optional<String>> request,
      ExecutionContext context) {

    try {
      String body = request.getBody().orElse("");
      Map<String, Object> bodyMap = objectMapper.readValue(body, Map.class);

      ExecutionInput executionInput = ExecutionInput.newExecutionInput()
          .query((String) bodyMap.get("query"))
          .variables((Map<String, Object>) bodyMap.getOrDefault("variables", Map.of()))
          .build();

      ExecutionResult result = graphQL.execute(executionInput);
      Map<String, Object> resultMap = result.toSpecification();

      return request.createResponseBuilder(HttpStatus.OK)
          .header("Content-Type", "application/json")
          .body(objectMapper.writeValueAsString(resultMap))
          .build();

    } catch (Exception e) {
      context.getLogger().severe("❌ Error ejecutando GraphQL: " + e.getMessage());
      return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error: " + e.getMessage())
          .build();
    }
  }

  private List<Map<String, Object>> generateMockLogs() {
    List<Map<String, Object>> logs = new ArrayList<>();

    for (int i = 1; i <= 10; i++) {
      Map<String, Object> user = new HashMap<>();
      user.put("name", "User " + i);
      user.put("email", "user" + i + "@example.com");

      List<Map<String, Object>> actions = new ArrayList<>();
      for (int j = 1; j <= 3; j++) {
        Map<String, Object> action = new HashMap<>();
        action.put("action", "UPDATE");
        action.put("timestamp", Instant.now().minusSeconds((long) (Math.random() * 100000)).toString());
        action.put("entity", "Product");

        List<Map<String, Object>> details = new ArrayList<>();
        details.add(Map.of("field", "price", "oldValue", "100", "newValue", String.valueOf(90 + i + j)));
        details.add(Map.of("field", "stock", "oldValue", "50", "newValue", String.valueOf(40 + j)));

        action.put("details", details);
        actions.add(action);
      }

      user.put("actions", actions);
      logs.add(user);
    }

    return logs;
  }
}
