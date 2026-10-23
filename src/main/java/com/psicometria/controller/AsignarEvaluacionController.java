package com.psicometria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psicometria.model.RequestRegistrarEvaluacion;
import com.psicometria.service.EvaluacionService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AsignarEvaluacionController implements HttpHandler {

    private final EvaluacionService evaluacionService = new EvaluacionService();
    private final ObjectMapper objectMapper = new ObjectMapper(); // Instancia de Jackson

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
            // Jackson lee el InputStream del cuerpo y lo convierte directamente a tu objeto Request
            RequestRegistrarEvaluacion requestData = objectMapper.readValue(exchange.getRequestBody(), RequestRegistrarEvaluacion.class);

            boolean exito = evaluacionService.guardarAsignacion(
                    requestData.getId_candidato_fk(),
                    requestData.getCombinacion(),
                    requestData.getNombre_evaluador()
            );

            if (exito) {
                respuestaMap.put("exito", true);
                respuestaMap.put("mensaje", "Evaluación programada correctamente");
            } else {
                respuestaMap.put("exito", false);
                respuestaMap.put("mensaje", "No se pudo programar la evaluación");
            }

        } catch (Exception e) {
            e.printStackTrace();
            respuestaMap.put("exito", false);
            respuestaMap.put("error", e.getMessage());
        }

        // Jackson convierte el Map de respuesta a JSON automáticamente
        String respuestaJson = objectMapper.writeValueAsString(respuestaMap);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = respuestaJson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}