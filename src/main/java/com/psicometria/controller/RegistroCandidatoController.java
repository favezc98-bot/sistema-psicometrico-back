package com.psicometria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psicometria.model.RequestRegistrarCandidato;
import com.psicometria.model.RequestRegistrarEvaluacion;
import com.psicometria.service.PuntuacionCandidatoService;
import com.psicometria.service.RegistroCandidatoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RegistroCandidatoController implements HttpHandler {

    private final RegistroCandidatoService registroCandidatoService = new RegistroCandidatoService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Cabeceras CORS
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
        int statusCode = 200; // <--- Variable para controlar el código HTTP

        try {
            RequestRegistrarCandidato requestData = objectMapper.readValue(exchange.getRequestBody(), RequestRegistrarCandidato.class);

            boolean exito = registroCandidatoService.registrarCandidatoConUsuario(requestData, requestData.getPassword());

            if (exito) {
                statusCode = 200;
                respuestaMap.put("exito", true);
                respuestaMap.put("mensaje", "Candidato registrado correctamente en el sistema");
            } else {
                statusCode = 409; // 409 Conflict (ideal para datos duplicados)
                respuestaMap.put("exito", false);
                respuestaMap.put("mensaje", "El correo electrónico ya se encuentra registrado en el sistema o los datos son inválidos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusCode = 500;
            respuestaMap.put("exito", false);
            respuestaMap.put("error", "Error interno en el servidor: " + e.getMessage());
        }

        // Convertimos la respuesta a JSON y la devolvemos con el statusCode correspondiente
        String respuestaJson = objectMapper.writeValueAsString(respuestaMap);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = respuestaJson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length); // <--- Usamos statusCode en lugar de 200 fijo

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
