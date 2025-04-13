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
 * Azure Function con GraphQL que simula una auditoría de cambios de roles
 * dentro de un sistema de usuarios y permisos.
 */
public class RoleAuditGraphQLFunction {

  // Utilizado para serializar/deserializar JSON
  private final ObjectMapper objectMapper = new ObjectMapper();

  // Motor de ejecución de consultas GraphQL
  private final GraphQL graphQL;

  public RoleAuditGraphQLFunction() {
    // Definición del tipo "RoleChange" que describe un cambio de rol individual
    GraphQLObjectType changeType = newObject()
        .name("RoleChange")
        .field(newFieldDefinition().name("action").type(GraphQLString)) // ADDED / REMOVED
        .field(newFieldDefinition().name("role").type(GraphQLString)) // nombre del rol
        .build();

    // Definición del tipo "RoleAudit" que representa una entrada de auditoría
    GraphQLObjectType auditLogType = newObject()
        .name("RoleAudit")
        .field(newFieldDefinition().name("performedBy").type(GraphQLString)) // quien realizó el cambio
        .field(newFieldDefinition().name("timestamp").type(GraphQLString)) // cuándo ocurrió
        .field(newFieldDefinition().name("targetUser").type(GraphQLString)) // a quién se aplicó
        .field(newFieldDefinition().name("changes").type(list(changeType))) // lista de cambios
        .build();

    // Definición del tipo raíz "Query"
    GraphQLObjectType queryType = newObject()
        .name("Query")
        .field(newFieldDefinition()
            .name("getRoleAuditTrail")
            .type(list(auditLogType)))
        .build();

    // Registro del data fetcher asociado a la query "getRoleAuditTrail"
    GraphQLCodeRegistry codeRegistry = GraphQLCodeRegistry.newCodeRegistry()
        .dataFetcher(
            FieldCoordinates.coordinates("Query", "getRoleAuditTrail"),
            (DataFetcher<?>) env -> generateMockAuditLogs())
        .build();

    // Construcción del esquema GraphQL completo
    GraphQLSchema schema = GraphQLSchema.newSchema()
        .query(queryType)
        .codeRegistry(codeRegistry)
        .build();

    // Inicialización del motor GraphQL
    this.graphQL = GraphQL.newGraphQL(schema).build();
  }

  @FunctionName("RoleAuditGraphQLFunction")
  public HttpResponseMessage run(
      @HttpTrigger(name = "req", methods = {
          HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS, route = "role-audit") HttpRequestMessage<Optional<String>> request,
      ExecutionContext context) {

    try {
      // Leer el cuerpo de la solicitud
      String body = request.getBody().orElse("");

      // Convertir el JSON recibido en un Map genérico
      Map<String, Object> bodyMap = objectMapper.readValue(body, new TypeReference<>() {
      });

      // Extraer variables si están presentes, asegurando tipo seguro
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

      // Preparar y ejecutar la consulta GraphQL
      ExecutionInput executionInput = ExecutionInput.newExecutionInput()
          .query((String) bodyMap.get("query"))
          .variables(variables)
          .build();

      ExecutionResult result = graphQL.execute(executionInput);
      Map<String, Object> resultMap = result.toSpecification();

      // Retornar la respuesta en formato JSON estándar de GraphQL
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
   * Genera 10 registros de auditoría simulados con cambios de rol.
   */
  private List<Map<String, Object>> generateMockAuditLogs() {
    List<Map<String, Object>> audits = new ArrayList<>();
    String[] admins = { "admin1", "admin2", "admin3" };
    String[] users = { "user1", "user2", "user3", "user4", "user5" };
    String[] roles = { "ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER" };
    String[] actions = { "ADDED", "REMOVED" };

    for (int i = 0; i < 10; i++) {
      Map<String, Object> entry = new HashMap<>();
      entry.put("performedBy", admins[i % admins.length]);
      entry.put("targetUser", users[i % users.length]);
      entry.put("timestamp", Instant.now().minusSeconds(i * 1000).toString());

      List<Map<String, Object>> changes = new ArrayList<>();
      for (int j = 0; j < 2; j++) {
        changes.add(Map.of(
            "action", actions[(i + j) % 2],
            "role", roles[(i + j) % roles.length]));
      }

      entry.put("changes", changes);
      audits.add(entry);
    }

    return audits;
  }
}
