package com.cloudnative;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
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
 * Azure Function con GraphQL que simula sesiones activas de usuarios.
 */
public class ActiveSessionsGraphQLFunction {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final GraphQL graphQL;

  public ActiveSessionsGraphQLFunction() {
    // Definición del tipo "ActiveSession"
    GraphQLObjectType sessionType = newObject()
        .name("ActiveSession")
        .field(newFieldDefinition().name("username").type(GraphQLString))
        .field(newFieldDefinition().name("role").type(GraphQLString))
        .field(newFieldDefinition().name("ipAddress").type(GraphQLString))
        .field(newFieldDefinition().name("sessionStart").type(GraphQLString))
        .field(newFieldDefinition().name("lastAction").type(GraphQLString))
        .build();

    // Definición de la query raíz
    GraphQLObjectType queryType = newObject()
        .name("Query")
        .field(newFieldDefinition()
            .name("getActiveSessions")
            .type(list(sessionType)))
        .build();

    // Registro del data fetcher
    GraphQLCodeRegistry codeRegistry = GraphQLCodeRegistry.newCodeRegistry()
        .dataFetcher(
            FieldCoordinates.coordinates("Query", "getActiveSessions"),
            (DataFetcher<?>) env -> generateMockSessions())
        .build();

    GraphQLSchema schema = GraphQLSchema.newSchema()
        .query(queryType)
        .codeRegistry(codeRegistry)
        .build();

    this.graphQL = GraphQL.newGraphQL(schema).build();
  }

  @FunctionName("ActiveSessionsGraphQLFunction")
  public HttpResponseMessage run(
      @HttpTrigger(name = "req", methods = {
          HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS, route = "active-sessions") HttpRequestMessage<Optional<String>> request,
      ExecutionContext context) {

    try {
      String body = request.getBody().orElse("");
      Map<String, Object> bodyMap = objectMapper.readValue(body, new TypeReference<>() {
      });

      Map<String, Object> variables = Map.of();
      Object variablesObj = bodyMap.get("variables");
      if (variablesObj instanceof Map<?, ?> rawMap) {
        Map<String, Object> safeMap = new HashMap<>();
        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
          if (entry.getKey() instanceof String key) {
            safeMap.put(key, entry.getValue());
          }
        }
        variables = safeMap;
      }

      ExecutionInput executionInput = ExecutionInput.newExecutionInput()
          .query((String) bodyMap.get("query"))
          .variables(variables)
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

  /**
   * Genera una lista mockeada de sesiones activas.
   */
  private List<Map<String, Object>> generateMockSessions() {
    List<Map<String, Object>> sessions = new ArrayList<>();
    String[] users = { "alice", "bob", "carla", "daniel", "emilio" };
    String[] roles = { "ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER" };
    String[] actions = { "Viewed dashboard", "Updated settings", "Accessed reports", "Changed password" };

    for (int i = 0; i < 10; i++) {
      Map<String, Object> session = new HashMap<>();
      session.put("username", users[i % users.length]);
      session.put("role", roles[i % roles.length]);
      session.put("ipAddress", "192.168.1." + (100 + i));
      session.put("sessionStart", Instant.now().minusSeconds(3000 + i * 200).toString());
      session.put("lastAction", actions[i % actions.length]);
      sessions.add(session);
    }

    return sessions;
  }
}
