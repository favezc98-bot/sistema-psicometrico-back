package com.psicometria.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // Extraído de panel de Aiven
    private static final String PARTE_1 = "AVNS_90ICDI";
    private static final String PARTE_2 = "jqgS1Bf4Te9SO";
    private static final String PASSWORD = PARTE_1 + PARTE_2;

    //configuraciones de conexiones
    private static final String URL = "jdbc:mariadb://mysql-1fa2a584-fav-0ce2.c.aivencloud.com:14905/defaultdb?sslMode=REQUIRED&trustServerCertificate=true";
    private static final String USUARIO = "avnadmin";

    public static Connection obtenerConexion() {
        Connection conexion = null;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el Driver de MariaDB -> " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos en Aiven -> " + e.getMessage());
        }
        return conexion;
    }
}