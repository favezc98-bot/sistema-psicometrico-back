package com.psicometria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psicometria.service.EvaluacionService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class BuscarCandidatoController implements HttpHandler {

    private final EvaluacionService evaluacionService = new EvaluacionService();
    private final ObjectMapper objectMapper = new ObjectMapper(); // Instancia de Jackson

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

        String query = exchange.getRequestURI().getQuery();
        String correo = "";
        if (query != null && query.startsWith("correo=")) {
            correo = query.substring(7);
        }

        Map<String, Object> resultado;
        if (!correo.isEmpty()) {
            resultado = evaluacionService.buscarCandidatoPorCorreo(correo);
        } else {
            resultado = Collections.singletonMap("encontrado", false);
            resultado.put("mensaje", "Correo no proporcionado");
        }

        // Jackson serializa el Map a JSON automáticamente
        String respuestaJson = objectMapper.writeValueAsString(resultado);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = respuestaJson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}