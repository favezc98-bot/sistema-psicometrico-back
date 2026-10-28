package com.psicometria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psicometria.model.ResponseConsultarCalificacion; // Tu DTO
import com.psicometria.model.ResponseConsultarCalificacion;
import com.psicometria.service.PuntuacionCandidatoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PuntuacionCandidatoController implements HttpHandler {

    private final PuntuacionCandidatoService puntuacionCandidatoService = new PuntuacionCandidatoService();

    // Constructor para inyectar el servicio si lo requieres
    public PuntuacionCandidatoController() {
    }

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

        // Obtener id_evaluacion de los parámetros de la URL (?id_evaluacion=1)
        String query = exchange.getRequestURI().getQuery();
        int idEvaluacion = 0;
        if (query != null && query.contains("id_evaluacion=")) {
            try {
                // Extracción segura por si vienen más parámetros en la URL
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("id_evaluacion=")) {
                        idEvaluacion = Integer.parseInt(param.substring(14));
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                idEvaluacion = 0;
            }
        }

        Map<String, Object> respuesta = new HashMap<>();
        if (idEvaluacion > 0) {
            //servicio para que calcule y devuelva el DTO de calificación
            ResponseConsultarCalificacion calificacion = puntuacionCandidatoService.obtenerPuntuacionCandidato(idEvaluacion);

            respuesta.put("encontrado", true);
            respuesta.put("resultado", calificacion);
        } else {
            respuesta.put("encontrado", false);
            respuesta.put("mensaje", "ID de evaluación no válido o no proporcionado");
        }

        String respuestaJson = objectMapper.writeValueAsString(respuesta);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = respuestaJson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}