package com.psicometria.admin;

import com.fasterxml.jackson.databind.ObjectMapper; // <-- Importante
import com.psicometria.controller.*;
import com.psicometria.model.*;
import com.psicometria.service.PreguntaService;
import com.psicometria.service.RespuestaService;
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
                //Configurar cabeceras CORS completas para cualquier método
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

                //Responder a la peticion previa OPTIONS (Pre-flight del navegador)
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                //Procesar la petición POST habitual
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

                    // Leemos el JSON y dejamos que Jackson lo mapee automáticamente al objeto RequestLogin
                    RequestLogin request = objectMapper.readValue(
                            exchange.getRequestBody(),
                            RequestLogin.class
                    );

                    LoginController controller = new LoginController();
                    ResponseConsultarUsuario usuario = controller.autenticar(request);

                    boolean acceso = (usuario != null);

                    String respuesta = acceso
                            ? "{\"status\": \"success\", \"mensaje\": \"Acceso concedido\", \"idUsuario\": " + usuario.getIdUsuario() + ", \"idRol\": " + usuario.getIdRolFk() + "}"
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
        servidor.createContext("/api/preguntas_vocabulario", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    PreguntaService servicio = new PreguntaService();
                    List<ResponsePreguntaVocabulario> preguntas = servicio.obtenerTodas();

                    //StringBuilder manual Jackson convierte la lista a json.
                    String respuestaJson = objectMapper.writeValueAsString(preguntas);

                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, respuestaJson.getBytes(StandardCharsets.UTF_8).length);

                    OutputStream os = exchange.getResponseBody();
                    os.write(respuestaJson.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            }
        });

        // --- ENDPOINT RESPUESTAS ---
        servidor.createContext("/api/respuestas_vocabulario", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                // Cabecera CORS para permitir la conexión con el frontend
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                // Permitir solicitudes OPTIONS para el pre-flight de CORS si es necesario
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    RespuestaService servicio = new RespuestaService();
                    List<ResponseRespuestaVocabulario> respuestas = servicio.obtenerRespuestasVocabulario();

                    // Jackson convierte la lista de objetos Java a un formato JSON limpio automáticamente
                    String respuestaJson = objectMapper.writeValueAsString(respuestas);

                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, respuestaJson.getBytes(StandardCharsets.UTF_8).length);

                    OutputStream os = exchange.getResponseBody();
                    os.write(respuestaJson.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            }
        });

        servidor.createContext("/api/preguntas_abstraccion", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    PreguntaService servicio = new PreguntaService();
                    List<ResponsePreguntaAbstraccion> preguntas = servicio.obtenerPreguntasAbstraccion();

                    String respuestaJson = objectMapper.writeValueAsString(preguntas);

                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, respuestaJson.getBytes(StandardCharsets.UTF_8).length);

                    OutputStream os = exchange.getResponseBody();
                    os.write(respuestaJson.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            }
        });

        servidor.createContext("/api/respuestas_abstraccion", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                // Cabecera CORS para permitir la conexión con el frontend
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                // Permitir solicitudes OPTIONS para el pre-flight de CORS si es necesario
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    RespuestaService servicio = new RespuestaService();
                    List<ResponseRespuestaAbstraccion> respuestas = servicio.obtenerRespuestasAbstraccion();
                    // Jackson convierte la lista de objetos Java a un formato JSON limpio automáticamente
                    String respuestaJson = objectMapper.writeValueAsString(respuestas);

                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, respuestaJson.getBytes(StandardCharsets.UTF_8).length);

                    OutputStream os = exchange.getResponseBody();
                    os.write(respuestaJson.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            }
        });

        servidor.createContext("/api/preguntas_bloque", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    PreguntaService servicio = new PreguntaService();
                    List<ResponsePreguntaBloque> preguntaBloque = servicio.obtenerPreguntasBloque();

                    String respuestaJson = objectMapper.writeValueAsString(preguntaBloque);

                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, respuestaJson.getBytes(StandardCharsets.UTF_8).length);

                    OutputStream os = exchange.getResponseBody();
                    os.write(respuestaJson.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            }
        });

        servidor.createContext("/api/respuestas_bloque", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                // Cabecera CORS para permitir la conexión con el frontend
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                // Permitir solicitudes OPTIONS para el pre-flight de CORS si es necesario
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    RespuestaService servicio = new RespuestaService();
                    List<ResponseRespuestaBloque> respuestasBloque = servicio.obtenerRespuestasBloque();

                    // Jackson convierte la lista de objetos Java a un formato JSON limpio automáticamente
                    String respuestaJson = objectMapper.writeValueAsString(respuestasBloque);

                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, respuestaJson.getBytes(StandardCharsets.UTF_8).length);

                    OutputStream os = exchange.getResponseBody();
                    os.write(respuestaJson.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            }
        });

        //Rutas de servicios creados
        servidor.createContext("/api/evaluacion/buscar", new BuscarCandidatoController());
        servidor.createContext("/api/evaluacion/asignar", new AsignarEvaluacionController());
        servidor.createContext("/api/evaluacion/combinacion-activa", new ObtenerCombinacionController());
        servidor.createContext("/api/evaluacion/guardar-respuestas", new GuardarRespuestasController());
        servidor.createContext("/api/evaluacion/consultar_puntuacion", new PuntuacionCandidatoController());

        servidor.setExecutor(null);
        servidor.start();
        System.out.println("Servidor listo. Ya puedes probar en Postman: POST http://localhost:8080/api/login");
    }
}