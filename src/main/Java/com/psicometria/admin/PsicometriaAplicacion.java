package com.psicometria.admin;

import com.fasterxml.jackson.databind.ObjectMapper; // <-- Importante
import com.psicometria.controller.LoginController;
import com.psicometria.model.RequestLogin;
import com.psicometria.model.ResponsePregunta;
import com.psicometria.service.PreguntaService;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PsicometriaAplicacion {

    private static final ObjectMapper objectMapper = new ObjectMapper(); // Instancia de Jackson

    public static void main(String[] args) throws IOException {
        HttpServer servidor = HttpServer.create(new InetSocketAddress(8080), 0);

        // --- ENDPOINT LOGIN ---
        servidor.createContext("/api/login", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

                    // Leemos el JSON y dejamos que Jackson lo mapee automáticamente al objeto RequestLogin
                    RequestLogin request = objectMapper.readValue(
                            exchange.getRequestBody(),
                            RequestLogin.class
                    );

                    LoginController controller = new LoginController();
                    boolean acceso = controller.autenticar(request);

                    String respuesta = acceso
                            ? "{\"status\": \"success\", \"mensaje\": \"Acceso concedido\"}"
                            : "{\"status\": \"error\", \"mensaje\": \"Credenciales incorrectas\"}";

                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(acceso ? 200 : 401, respuesta.getBytes(StandardCharsets.UTF_8).length);

                    OutputStream os = exchange.getResponseBody();
                    os.write(respuesta.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            }
        });

        // --- ENDPOINT PREGUNTAS ---
        servidor.createContext("/api/preguntas", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    PreguntaService servicio = new PreguntaService();
                    List<ResponsePregunta> preguntas = servicio.obtenerTodas();

                    // ¡Adiós al StringBuilder manual! Jackson convierte la lista a JSON por ti de inmediato.
                    String respuestaJson = objectMapper.writeValueAsString(preguntas);

                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, respuestaJson.getBytes(StandardCharsets.UTF_8).length);

                    OutputStream os = exchange.getResponseBody();
                    os.write(respuestaJson.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            }
        });

        servidor.setExecutor(null);
        servidor.start();
        System.out.println("Servidor listo. Ya puedes probar en Postman: POST http://localhost:8080/api/login");
    }
}