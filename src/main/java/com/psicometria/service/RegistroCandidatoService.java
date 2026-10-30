package com.psicometria.service;

import com.psicometria.conexion.ConexionBD;
import com.psicometria.model.RequestRegistrarCandidato;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class RegistroCandidatoService {

    public boolean registrarCandidatoConUsuario(RequestRegistrarCandidato req, String passwordPlano) throws SQLException {
        String sqlUsuario = "INSERT INTO usuarios (id_rol_fk, correo, password, activo) VALUES (?, ?, ?, 1)";
        String sqlCandidato = "INSERT INTO candidato (id_usuario_fk, nombre, apellido, correo, telefono, domicilio, fecha_nacimiento) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            // Usamos clase centralizada de conexión
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false); // Transacción manual para integridad

            //Insertar en la tabla usuarios (Rol 2 = USUARIO / Candidato)
            String hashedPassword = BCrypt.hashpw(passwordPlano, BCrypt.gensalt());
            int idGeneradoUsuario = 0;

            try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                stmtUsuario.setInt(1, 2); // Rol 2 por defecto
                stmtUsuario.setString(2, req.getCorreo());
                stmtUsuario.setString(3, hashedPassword);

                int filasAfectadas = stmtUsuario.executeUpdate();
                if (filasAfectadas == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet generatedKeys = stmtUsuario.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idGeneradoUsuario = generatedKeys.getInt(1);
                    } else {
                        conn.rollback();
                        throw new SQLException("Fallo al obtener el ID del usuario creado.");
                    }
                }
            }

            //Insertar en la tabla candidato usando el id_usuario obtenido
            try (PreparedStatement stmtCandidato = conn.prepareStatement(sqlCandidato)) {
                stmtCandidato.setLong(1, idGeneradoUsuario);
                stmtCandidato.setString(2, req.getNombre());
                stmtCandidato.setString(3, req.getApellido());
                stmtCandidato.setString(4, req.getCorreo());
                stmtCandidato.setString(5, req.getTelefono());
                stmtCandidato.setString(6,req.getDomicilio());
                stmtCandidato.setString(7,req.getFecha_nacimiento());

                stmtCandidato.executeUpdate();
            }

            conn.commit(); // Todo salió bien, guardamos cambios
            conn.close();
            return true;

        } catch (Exception e) {
            System.err.println("Error al registrar candidato y usuario: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Si algo falla, revertimos todo
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        }
    }
}
