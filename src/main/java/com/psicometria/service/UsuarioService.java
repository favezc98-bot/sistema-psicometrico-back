package com.psicometria.service;

import com.psicometria.conexion.ConexionBD;
import com.psicometria.model.RequestLogin;
import com.psicometria.model.ResponseConsultarUsuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioService {

    // Cambiamos el retorno a Usuario en lugar de boolean
    public ResponseConsultarUsuario validarCredenciales(RequestLogin request) {
        String sql = "SELECT id_usuario, id_rol_fk, password FROM usuarios WHERE correo = ? AND activo = 1";

        try {
            Connection conexion = ConexionBD.obtenerConexion();
            PreparedStatement pstmt = conexion.prepareStatement(sql);

            pstmt.setString(1, request.getCorreo());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String passwordHashBD = rs.getString("password");
                int idUsuario = rs.getInt("id_usuario");
                int idRolFk = rs.getInt("id_rol_fk");

                rs.close();
                pstmt.close();
                conexion.close();

                // Comparamos la contraseña con BCrypt
                if (BCrypt.checkpw(request.getPassword(), passwordHashBD)) {
                    // Si la contraseña coincide, creamos y retornamos el objeto Usuario con sus roles
                    ResponseConsultarUsuario consultarUsuario = new ResponseConsultarUsuario();
                    consultarUsuario.setIdUsuario(idUsuario);
                    consultarUsuario.setIdRolFk(idRolFk);
                    consultarUsuario.setCorreo(request.getCorreo());
                    return consultarUsuario;
                }
            } else {
                rs.close();
                pstmt.close();
                conexion.close();
            }

        } catch (Exception e) {
            System.out.println("Error en la base de datos: " + e.getMessage());
        }

        // Si falla la contraseña o no existe el correo, retornamos null
        return null;
    }
}