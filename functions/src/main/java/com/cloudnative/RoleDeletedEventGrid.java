package com.cloudnative;

import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;


public class RoleDeletedEventGrid {

    private static final Gson GSON = new Gson();
    
    @FunctionName("RoleDeletedEventGrid")
    public void run(
            @EventGridTrigger(name = "event") String payload,
            final ExecutionContext context) {

        Logger log = context.getLogger();
        log.info("RoleDeletedEventGrid ejecutada");

       try {
            log.info(String.format("RAW payload: %s", payload));

            JsonObject ev = GSON.fromJson(payload, JsonObject.class);
            String eventType = ev.has("eventType") ? ev.get("eventType").getAsString()
                    : ev.has("type") ? ev.get("type").getAsString() : "unknown";

            if (!"role_deleted".equalsIgnoreCase(eventType)) {
                log.info(String.format("Evento ignorado (eventType=%s)", eventType));
                return;
            }

            if (ev.has("data")) {
                log.info(String.format("Rol Eliminado. Contenido del evento: %s", ev.get("data").toString()));
            } else {
                log.warning("El evento no contiene campo 'data'");
            }

        } catch (Exception ex) {
            log.severe(String.format("Error al procesar evento en RoleDeletedEventGrid: %s",
                    ex.getMessage()));
            throw new RuntimeException(ex);
        }
    }
}
