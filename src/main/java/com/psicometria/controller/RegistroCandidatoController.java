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

        try {
            // Leemos directamente el JSON del cuerpo y lo convertimos a nuestro DTO
            RequestRegistrarCandidato requestData = objectMapper.readValue(exchange.getRequestBody(), RequestRegistrarCandidato.class);

            // Nota: Asegúrate de que tu DTO RequestRegistrarCandidato incluya un campo 'password'
            // o que lo pases desde el front. Aquí asumimos que requestData.getPassword() trae la contraseña.
            boolean exito = registroCandidatoService.registrarCandidatoConUsuario(requestData, requestData.getPassword());

            if (exito) {
                respuestaMap.put("exito", true);
                respuestaMap.put("mensaje", "Candidato registrado correctamente en el sistema");
            } else {
                respuestaMap.put("exito", false);
                respuestaMap.put("mensaje", "No se pudo completar el registro del candidato");
            }

        } catch (Exception e) {
            e.printStackTrace();
            respuestaMap.put("exito", false);
            respuestaMap.put("error", e.getMessage());
        }

        // 2. Convertimos la respuesta a JSON y la devolvemos
        String respuestaJson = objectMapper.writeValueAsString(respuestaMap);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = respuestaJson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
