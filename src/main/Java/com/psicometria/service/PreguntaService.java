package com.psicometria.service;

import com.psicometria.conexion.ConexionBD;
import com.psicometria.model.ResponsePreguntaAbstraccion;
import com.psicometria.model.ResponsePreguntaVocabulario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
}