package com.psicometria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psicometria.service.EvaluacionService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ObtenerCombinacionController implements HttpHandler {

    private final EvaluacionService evaluacionService = new EvaluacionService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        // Obtener id_candidato de los parámetros de la URL (?id_candidato=1)
        String query = exchange.getRequestURI().getQuery();
        int idCandidato = 0;
        if (query != null && query.contains("id_candidato=")) {
            try {
                // Extracción más segura por si vienen más parámetros en la URL
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("id_candidato=")) {
                        idCandidato = Integer.parseInt(param.substring(13));
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                idCandidato = 0;
            }
        }

        Map<String, Object> respuesta = new HashMap<>();
        if (idCandidato > 0) {
            // AQUÍ CAMBIAMOS: Llamamos a un método que nos devuelva un Map con { id_evaluacion, combinacion }
            Map<String, Object> datosEvaluacion = evaluacionService.obtenerDatosUltimaEvaluacion(idCandidato);

            if (datosEvaluacion != null && !datosEvaluacion.isEmpty()) {
                respuesta.put("encontrado", true);
                respuesta.put("combinacion", datosEvaluacion.get("combinacion"));
                respuesta.put("id_evaluacion", datosEvaluacion.get("id_evaluacion")); // <--- ¡Esto faltaba!
            } else {
                respuesta.put("encontrado", false);
                respuesta.put("mensaje", "No se encontró ninguna evaluación programada para este candidato.");
            }
        } else {
            respuesta.put("encontrado", false);
            respuesta.put("mensaje", "ID de candidato no válido o no proporcionado");
        }

        String respuestaJson = objectMapper.writeValueAsString(respuesta);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = respuestaJson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}