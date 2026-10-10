package com.psicometria.admin;

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

    public static void main(String[] args) throws IOException {
        // Levantamos un servidor nativo en el puerto 8080 (sin librerías extra)
        HttpServer servidor = HttpServer.create(new InetSocketAddress(8080), 0);

        servidor.createContext("/api/login", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

                    //Leemos el JSON que manda Postman
                    String bodyJson = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                    //Extraemos los datos usando nuestro método auxiliar corregido
                    RequestLogin request = new RequestLogin();
                    request.setCorreo(obtenerValorJson(bodyJson, "correo"));
                    request.setPassword(obtenerValorJson(bodyJson, "password"));

                    //Probamos con tu controlador y servicio existente
                    LoginController controller = new LoginController();
                    boolean acceso = controller.autenticar(request);

                    //Armamos la respuesta en formato JSON para Postman
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

        servidor.createContext("/api/preguntas", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    PreguntaService servicio = new PreguntaService();
                    List<ResponsePregunta> preguntas = servicio.obtenerTodas();

                    // Construimos el JSON de forma manual (tipo arreglo [...]) sin librerías externas
                    StringBuilder jsonBuilder = new StringBuilder();
                    jsonBuilder.append("[");

                    for (int i = 0; i < preguntas.size(); i++) {
                        ResponsePregunta pregunta = preguntas.get(i);
                        jsonBuilder.append("{");
                        jsonBuilder.append("\"id\": ").append(pregunta.getIdPreguntaTest()).append(",");
                        jsonBuilder.append("\"tipo\": \"").append(pregunta.getTipoTest()).append("\",");
                        jsonBuilder.append("\"reactivo\": ").append(pregunta.getNumeroReactivo()).append(",");
                        jsonBuilder.append("\"pregunta\": \"").append(pregunta.getPregunta()).append("\"");
                        jsonBuilder.append("}");
                        if (i < preguntas.size() - 1) {
                            jsonBuilder.append(",");
                        }
                    }
                    jsonBuilder.append("]");

                    String respuestaJson = jsonBuilder.toString();

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

    // Método auxiliar con los parámetros correctamente tipados (String json, String campo)
    private static String obtenerValorJson(String json, String campo) {
        try {
            String clave = "\"" + campo + "\"";
            int idx = json.indexOf(clave);
            if (idx == -1) return "";
            int start = json.indexOf(":", idx) + 1;
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return json.substring(start, end).replace("\"", "").trim();
        } catch (Exception e) {
            return "";
        }
    }
}