package com.psicometria.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import modelo.RespuestaCandidato; // Tu modelo existente
import com.psicometria.service.EvaluacionService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuardarRespuestasController implements HttpHandler {

    private final EvaluacionService evaluacionService = new EvaluacionService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        Map<String, Object> respuestaMap = new HashMap<>();

        try {
            // 1. Leer el cuerpo de la petición como un árbol JSON genérico (JsonNode)
            JsonNode jsonNode = objectMapper.readTree(exchange.getRequestBody());

            // 2. Extraer el idCandidato directamente del JSON
            int idCandidato = jsonNode.get("idCandidato").asInt();

            // 3. Mapear el arreglo "respuestas" directamente a una lista de tu objeto RespuestaCandidato
            List<RespuestaCandidato> listaRespuestas = objectMapper.readValue(
                    jsonNode.get("respuestas").traverse(objectMapper),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, RespuestaCandidato.class)
            );

            // 4. Invocar tu servicio que ya tienes listo
            boolean exito = evaluacionService.guardarRespuestasYFinalizar(idCandidato, listaRespuestas);

            if (exito) {
                respuestaMap.put("exito", true);
                respuestaMap.put("mensaje", "Respuestas guardadas y prueba finalizada con éxito");
            } else {
                respuestaMap.put("exito", false);
                respuestaMap.put("mensaje", "No se pudieron guardar las respuestas");
            }

        } catch (Exception e) {
            e.printStackTrace();
            respuestaMap.put("exito", false);
            respuestaMap.put("error", e.getMessage());
        }

        // Convertir respuesta a JSON y enviar
        String respuestaJson = objectMapper.writeValueAsString(respuestaMap);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = respuestaJson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}