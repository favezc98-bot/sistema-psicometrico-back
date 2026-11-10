package com.psicometria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psicometria.model.ResponseResultadoEvaluacion;
import com.psicometria.repository.PuntuacionCandidatoRepository;
import com.psicometria.service.EvaluacionService;
import com.psicometria.service.PuntuacionCandidatoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuntuacionCandidatoController implements HttpHandler {

    private final PuntuacionCandidatoService puntuacionCandidatoService = new PuntuacionCandidatoService();
    private final EvaluacionService evaluacionService = new EvaluacionService();
    private final PuntuacionCandidatoRepository repository = new PuntuacionCandidatoRepository();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PuntuacionCandidatoController() {
    }

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

        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        Map<String, Object> respuesta = new HashMap<>();

        try {
            if (path.contains("/buscar_candidato")) {
                // Ruta para buscar al candidato por correo y extraer su historial
                String correo = params.get("correo");
                if (correo != null && !correo.isEmpty()) {
                    Map<String, Object> candidato = evaluacionService.buscarCandidatoPorCorreo(correo);
                    if (candidato.containsKey("encontrado") && (Boolean) candidato.get("encontrado")) {
                        int idCandidato = (int) candidato.get("id_candidato");

                        // Obtenemos las evaluaciones básicas del candidato
                        List<Map<String, Object>> historialBasico = repository.obtenerHistorialPorCandidato(idCandidato);
                        List<Map<String, Object>> historialConResultados = new ArrayList<>();

                        // Recorremos cada evaluación para adjuntarle su resultado completo de pruebas
                        for (Map<String, Object> ev : historialBasico) {
                            int idEvaluacion = (int) ev.get("idEvaluacion");
                            ResponseResultadoEvaluacion resultadoCompleto = puntuacionCandidatoService.calcularResultadoCompleto(idEvaluacion);

                            Map<String, Object> evaluacionConDetalle = new java.util.HashMap<>();
                            evaluacionConDetalle.put("idEvaluacion", idEvaluacion);
                            evaluacionConDetalle.put("combinacion", ev.get("combinacion"));
                            evaluacionConDetalle.put("estado", ev.get("estado"));
                            evaluacionConDetalle.put("fechaEvaluacion", ev.get("fechaEvaluacion"));
                            // Aquí inyectamos todo el objeto de pruebas, percentiles y combinación
                            evaluacionConDetalle.put("resultado", resultadoCompleto);

                            historialConResultados.add(evaluacionConDetalle);
                        }

                        candidato.put("historial", historialConResultados);
                        respuesta.put("encontrado", true);
                        respuesta.put("resultado", candidato); // O response plano según tu preferencia
                    } else {
                        respuesta.put("encontrado", false);
                        respuesta.put("mensaje", "Candidato no encontrado");
                    }
                } else {
                    respuesta.put("encontrado", false);
                    respuesta.put("mensaje", "Correo no proporcionado");
                }
            } else {
                // Ruta original para consultar el reporte detallado por ID de evaluación
                int idEvaluacion = params.containsKey("id_evaluacion") ? Integer.parseInt(params.get("id_evaluacion")) : 0;
                if (idEvaluacion > 0) {
                    ResponseResultadoEvaluacion calificacion = puntuacionCandidatoService.calcularResultadoCompleto(idEvaluacion);
                    respuesta.put("encontrado", true);
                    respuesta.put("resultado", calificacion);
                } else {
                    respuesta.put("encontrado", false);
                    respuesta.put("mensaje", "ID de evaluación no válido o no proporcionado");
                }
            }

            enviarJson(exchange, respuesta, 200);

        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("encontrado", false);
            respuesta.put("error", "Error interno en el servidor: " + e.getMessage());
            enviarJson(exchange, respuesta, 500);
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1) {
                    map.put(pair[0], pair[1]);
                } else {
                    map.put(pair[0], "");
                }
            }
        }
        return map;
    }

    private void enviarJson(HttpExchange exchange, Object objeto, int status) throws IOException {
        String json = objectMapper.writeValueAsString(objeto);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}