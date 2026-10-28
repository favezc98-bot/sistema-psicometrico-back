package com.psicometria.service;

import com.psicometria.conexion.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluacionService {

    //Buscar candidato y retornar un Map
    public Map<String, Object> buscarCandidatoPorCorreo(String correo) {
        Map<String, Object> resultado = new HashMap<>();
        String sql = "SELECT c.id_candidato, c.nombre, c.apellido, u.correo " +
                "FROM candidato c " +
                "JOIN usuarios u ON c.id_usuario_fk = u.id_usuario " +
                "WHERE u.correo = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                resultado.put("encontrado", true);
                resultado.put("id_candidato", rs.getInt("id_candidato"));
                resultado.put("nombre", rs.getString("nombre"));
                resultado.put("apellido", rs.getString("apellido"));
                resultado.put("correo", rs.getString("correo"));
            } else {
                resultado.put("encontrado", false);
                resultado.put("mensaje", "Candidato no encontrado");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultado.put("encontrado", false);
            resultado.put("error", e.getMessage());
        }

        return resultado;
    }

    //Guardar asignación (Evaluador)
    public boolean guardarAsignacion(int idCandidato, String combinacion, String nombreEvaluador) {
        String sql = "INSERT INTO evaluacion (id_candidato_fk, combinacion, estado, fecha_evaluacion, nombre_evaluador) " +
                "VALUES (?, ?, 'PROGRAMADA', NOW(), ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCandidato);
            stmt.setString(2, combinacion);
            stmt.setString(3, nombreEvaluador);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Obtener la combinación solo si la evaluación sigue en estado 'PROGRAMADA'
    public String obtenerUltimaCombinacion(int idCandidato) {
        String sql = "SELECT combinacion FROM evaluacion WHERE id_candidato_fk = ? AND estado = 'PROGRAMADA' ORDER BY id_evaluacion DESC LIMIT 1";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCandidato);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("combinacion");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Retornar null si no hay ninguna prueba 'PROGRAMADA' activa
    }

    // 4. Guardar paquete masivo de respuestas y cambiar estado a FINALIZADA
    public boolean guardarRespuestasYFinalizar(int idCandidato, List<modelo.RespuestaCandidato> respuestas) {
        String sqlInsert = "INSERT INTO respuesta_candidato (id_evaluacion_fk, id_pregunta_test, sub_parte, respuesta_seleccionada) VALUES (?, ?, ?, ?)";
        String sqlUpdateEstado = "UPDATE evaluacion SET estado = 'FINALIZADA' WHERE id_candidato_fk = ? AND estado = 'PROGRAMADA'";

        Connection conn = null;

        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false); // Transacción manual

            // A. Inserción masiva usando el modelo RespuestaCandidato
            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                for (modelo.RespuestaCandidato item : respuestas) {
                    stmtInsert.setInt(1, idCandidato);
                    stmtInsert.setInt(2, item.getIdPreguntaTest());
                    stmtInsert.setString(3, item.getSubParte());
                    stmtInsert.setString(4, item.getRespuestaSeleccionada());
                    stmtInsert.addBatch();
                }
                stmtInsert.executeBatch();
            }

            // B. Cambiar el estado de la evaluación a FINALIZADA
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdateEstado)) {
                stmtUpdate.setInt(1, idCandidato);
                stmtUpdate.executeUpdate();
            }

            conn.commit(); // Todo exitoso, guardamos cambios en BD
            conn.close();
            return true;

        } catch (Exception e) {
            System.err.println("Error al guardar respuestas y finalizar evaluación: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Si algo falla, se revierte todo
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        }
    }
}