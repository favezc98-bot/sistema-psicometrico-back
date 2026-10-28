package com.psicometria.repository;

import com.psicometria.conexion.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuntuacionCandidatoRepository {

    // Obtener los aciertos de vocabulario para una evaluación
    public int contarAciertosVocabulario(int idEvaluacion) {
        String sql = "SELECT COUNT(*) AS aciertos " +
                "FROM respuesta_candidato rc " +
                "JOIN respuesta_test rt ON rt.id_pregunta_test = rc.id_pregunta_test " +
                "AND rc.respuesta_seleccionada = rt.respuesta " +
                "JOIN pregunta_test p ON p.id_pregunta_test = rc.id_pregunta_test " +
                "WHERE rc.id_evaluacion_fk = ? AND p.tipo_test = 'VOCABULARIO' AND rt.valida = 1";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvaluacion);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("aciertos");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int contarTotalVocabulario(int idEvaluacion) {
        String sql = "SELECT COUNT(DISTINCT rc.id_pregunta_test) AS total " +
                "FROM respuesta_candidato rc " +
                "JOIN pregunta_test p ON p.id_pregunta_test = rc.id_pregunta_test " +
                "WHERE rc.id_evaluacion_fk = ? AND p.tipo_test = 'VOCABULARIO'";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvaluacion);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Obtener respuestas de abstracción para evaluar en memoria
    public List<Map<String, String>> obtenerRespuestasAbstraccion(int idEvaluacion) {
        List<Map<String, String>> lista = new ArrayList<>();
        String sql = "SELECT rc.respuesta_seleccionada, rt.respuesta AS respuesta_correcta " +
                "FROM respuesta_candidato rc " +
                "JOIN respuesta_test rt ON rt.id_pregunta_test = rc.id_pregunta_test " +
                "JOIN pregunta_test p ON p.id_pregunta_test = rc.id_pregunta_test " +
                "WHERE rc.id_evaluacion_fk = ? AND p.tipo_test = 'ABSTRACCION'";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvaluacion);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("usuario", rs.getString("respuesta_seleccionada"));
                    map.put("correcta", rs.getString("respuesta_correcta"));
                    lista.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Obtener respuestas de bloques para evaluar
    public List<Map<String, String>> obtenerRespuestasBloques(int idEvaluacion) {
        List<Map<String, String>> lista = new ArrayList<>();
        String sql = "SELECT rc.respuesta_seleccionada, rt.opcion " +
                "FROM respuesta_candidato rc " +
                "JOIN respuesta_test rt ON rt.id_pregunta_test = rc.id_pregunta_test " +
                "JOIN pregunta_test p ON p.id_pregunta_test = rc.id_pregunta_test " +
                "WHERE rc.id_evaluacion_fk = ? AND p.tipo_test = 'BLOQUE' AND rt.valida = 1";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvaluacion);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("usuario", rs.getString("respuesta_seleccionada"));
                    map.put("opcionCorrecta", rs.getString("opcion"));
                    lista.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}