package com.psicometria.service;

import com.psicometria.conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class EvaluacionService {

    // Buscar candidato y retornar un Map (Jackson lo convierte a JSON perfecto)
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

    // Guardar asignación
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

    // Obtener la última combinación asignada al candidato
    public String obtenerUltimaCombinacion(int idCandidato) {
        String sql = "SELECT combinacion FROM evaluacion WHERE id_candidato_fk = ? ORDER BY id_evaluacion DESC LIMIT 1";

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
        return "BC"; // Valor por defecto si no encuentra registros
    }
}