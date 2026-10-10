package com.psicometria.service;

import com.psicometria.conexion.ConexionBD;
import com.psicometria.model.RequestLogin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioService {

    public boolean validarCredenciales(RequestLogin request) {
        String sql = "SELECT password FROM usuarios WHERE correo = ? AND activo = 1";

        try {
            // 1. Abrimos la conexión de forma directa
            Connection conexion = ConexionBD.obtenerConexion();
            PreparedStatement pstmt = conexion.prepareStatement(sql);

            //Asignamos el correo
            pstmt.setString(1, request.getCorreo());

            //Ejecutamos la consulta
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String passwordHashBD = rs.getString("password");

                // Cerramos manualmente lo que abrimos para ser ordenados
                rs.close();
                pstmt.close();
                conexion.close();

                // Comparamos la contraseña
                return passwordHashBD.equals(request.getPassword());
            }

            // Cerramos si no entró al if
            rs.close();
            pstmt.close();
            conexion.close();

        } catch (Exception e) {
            System.out.println("Error en la base de datos");
        }

        return false;
    }
}