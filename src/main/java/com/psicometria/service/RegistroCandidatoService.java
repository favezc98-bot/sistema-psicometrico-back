package com.psicometria.service;

import com.psicometria.conexion.ConexionBD;
import com.psicometria.model.RequestRegistrarCandidato;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class RegistroCandidatoService {

    public boolean registrarCandidatoConUsuario(RequestRegistrarCandidato req, String passwordPlano) throws SQLException {

        if (req.getCorreo() == null || req.getCorreo().trim().isEmpty()) {
            System.out.println("El correo no puede estar vacío.");
            return false;
        }

        String correoLimpio = req.getCorreo().trim();
        String sqlCheck = "SELECT COUNT(*) FROM usuarios WHERE correo = ?";
        String sqlUsuario = "INSERT INTO usuarios (id_rol_fk, correo, password, activo) VALUES (?, ?, ?, 1)";
        String sqlCandidato = "INSERT INTO candidato (id_usuario_fk, nombre, apellido, correo, telefono, domicilio, fecha_nacimiento) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();

            //VALIDAR SI EL CORREO YA EXISTE ANTES DE HACER NADA
            try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck)) {
                stmtCheck.setString(1, correoLimpio);
                try (ResultSet rs = stmtCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("El correo ya existe en el sistema: " + correoLimpio);
                        conn.close();
                        return false; // Frena aquí y no toca los INSERT
                    }
                }
            }

            //SI NO EXISTE, INICIA LA TRANSACCIÓN E INSERTA
            conn.setAutoCommit(false);

            String hashedPassword = BCrypt.hashpw(passwordPlano, BCrypt.gensalt());
            int idGeneradoUsuario = 0;

            try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                stmtUsuario.setInt(1, 2); // Rol 2 por defecto
                stmtUsuario.setString(2, correoLimpio);
                stmtUsuario.setString(3, hashedPassword);

                int filasAfectadas = stmtUsuario.executeUpdate();
                if (filasAfectadas == 0) {
                    conn.rollback();
                    conn.close();
                    return false;
                }

                try (ResultSet generatedKeys = stmtUsuario.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idGeneradoUsuario = generatedKeys.getInt(1);
                    } else {
                        conn.rollback();
                        conn.close();
                        throw new SQLException("Fallo al obtener el ID del usuario creado.");
                    }
                }
            }

            try (PreparedStatement stmtCandidato = conn.prepareStatement(sqlCandidato)) {
                stmtCandidato.setLong(1, idGeneradoUsuario);
                stmtCandidato.setString(2, req.getNombre());
                stmtCandidato.setString(3, req.getApellido());
                stmtCandidato.setString(4, correoLimpio);
                stmtCandidato.setString(5, req.getTelefono());
                stmtCandidato.setString(6, req.getDomicilio());
                stmtCandidato.setString(7, req.getFecha_nacimiento());

                stmtCandidato.executeUpdate();
            }

            conn.commit();
            conn.close();
            return true;

        } catch (Exception e) {
            System.err.println("Error al registrar candidato y usuario: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        }
    }
}