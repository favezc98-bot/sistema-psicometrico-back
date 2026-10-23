package com.psicometria.service;

import com.psicometria.conexion.ConexionBD;
import com.psicometria.model.ResponsePreguntaAbstraccion;
import com.psicometria.model.ResponsePreguntaBloque;
import com.psicometria.model.ResponsePreguntaVocabulario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PreguntaService {

    public List<ResponsePreguntaVocabulario> obtenerTodas() {
        List<ResponsePreguntaVocabulario> lista = new ArrayList<>();
        // FILTRAR POR VOCABULARIO
        String sql = "SELECT id_pregunta_test, tipo_test, numero_reactivo, pregunta FROM pregunta_test WHERE tipo_test = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            // Pasamos el parámetro de manera segura con PreparedStatement
            pstmt.setString(1, "VOCABULARIO");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ResponsePreguntaVocabulario pregunta = new ResponsePreguntaVocabulario();
                    pregunta.setIdPreguntaTest(rs.getInt("id_pregunta_test"));
                    pregunta.setTipoTest(rs.getString("tipo_test"));
                    pregunta.setNumeroReactivo(rs.getInt("numero_reactivo"));
                    pregunta.setPregunta(rs.getString("pregunta"));
                    lista.add(pregunta);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al consultar preguntas vocabulario ");
        }

        return lista;
    }

    public List<ResponsePreguntaAbstraccion> obtenerPreguntasAbstraccion() {
        List<ResponsePreguntaAbstraccion> lista = new ArrayList<>();
        // FILTRAR POR ABSTRACCION
        String sql = "SELECT id_pregunta_test, tipo_test, numero_reactivo, pregunta FROM pregunta_test WHERE tipo_test = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            // Pasamos el parámetro para abstracción
            pstmt.setString(1, "ABSTRACCION");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ResponsePreguntaAbstraccion pregunta = new ResponsePreguntaAbstraccion();
                    pregunta.setIdPreguntaTest(rs.getInt("id_pregunta_test"));
                    pregunta.setTipoTest(rs.getString("tipo_test"));
                    pregunta.setNumeroReactivo(rs.getInt("numero_reactivo"));
                    pregunta.setPregunta(rs.getString("pregunta"));
                    lista.add(pregunta);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al consultar preguntas abstraccion ");
        }
        return lista;
    }

    public List<ResponsePreguntaBloque> obtenerPreguntasBloque() {
        // Usamos un Map ordenado para agrupar por numero_reactivo
        Map<Integer, ResponsePreguntaBloque> mapaAgrupado = new LinkedHashMap<>();

        String sql = "SELECT id_pregunta_test, tipo_test, numero_reactivo, pregunta FROM pregunta_test WHERE tipo_test = ? ORDER BY numero_reactivo, id_pregunta_test";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            pstmt.setString(1, "BLOQUE");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int numeroReactivo = rs.getInt("numero_reactivo");

                    // Si el reactivo ya existe en el mapa, significa que es la segunda parte (pregunta doble)
                    if (mapaAgrupado.containsKey(numeroReactivo)) {
                        ResponsePreguntaBloque existente = mapaAgrupado.get(numeroReactivo);
                        // Puedes almacenar el segundo subitem o la segunda imagen si tu modelo lo soporta,
                        // o asegurarte de que el frontend cuente los subitems > 1.
                        // *Nota: Asegúrate de que tu modelo ResponsePreguntaBloque soporte lista de subitems si lo manejas así en el back.*
                    } else {
                        ResponsePreguntaBloque pregunta = new ResponsePreguntaBloque();
                        pregunta.setIdPreguntaTest(rs.getInt("id_pregunta_test"));
                        pregunta.setTipoTest(rs.getString("tipo_test"));
                        pregunta.setNumeroReactivo(numeroReactivo);
                        pregunta.setPregunta(rs.getString("pregunta"));
                        mapaAgrupado.put(numeroReactivo, pregunta);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error al consultar preguntas de test bloque: " + e.getMessage());
        }

        return new ArrayList<>(mapaAgrupado.values());
    }
}