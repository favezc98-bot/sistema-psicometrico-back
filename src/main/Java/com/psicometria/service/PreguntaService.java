package com.psicometria.service;

import com.psicometria.conexion.ConexionBD;
import com.psicometria.model.ResponsePregunta;
import com.psicometria.model.ResponsePregunta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PreguntaService {

    public List<ResponsePregunta> obtenerTodas() {
        List<ResponsePregunta> lista = new ArrayList<>();
        String sql = "SELECT id_pregunta_test, tipo_test, numero_reactivo, pregunta FROM pregunta_test";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ResponsePregunta pregunta = new ResponsePregunta();
                pregunta.setIdPreguntaTest(rs.getInt("id_pregunta_test"));
                pregunta.setTipoTest(rs.getString("tipo_test"));
                pregunta.setNumeroReactivo(rs.getInt("numero_reactivo"));
                pregunta.setPregunta(rs.getString("pregunta"));
                lista.add(pregunta);
            }

        } catch (Exception e) {
            System.out.println("Error al consultar preguntas: " + e.getMessage());
        }

        return lista;
    }
}