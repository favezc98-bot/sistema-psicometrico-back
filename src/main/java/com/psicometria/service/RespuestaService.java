package com.psicometria.service;

import com.psicometria.conexion.ConexionBD;
import com.psicometria.model.ResponseRespuestaAbstraccion;
import com.psicometria.model.ResponseRespuestaVocabulario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RespuestaService {

    public List<ResponseRespuestaVocabulario> obtenerRespuestasVocabulario() {
        List<ResponseRespuestaVocabulario> respuestaVocabularioList = new ArrayList<>();

        //JOIN con pregunta_test filtrando por VOCABULARIO
        String sql = "SELECT r.id_respuesta_test, r.id_pregunta_test, r.opcion, r.respuesta, r.valida " +
                "FROM respuesta_test r " +
                "JOIN pregunta_test p ON r.id_pregunta_test = p.id_pregunta_test " +
                "WHERE p.tipo_test = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            pstmt.setString(1, "VOCABULARIO");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ResponseRespuestaVocabulario respuestaVocabulario = new ResponseRespuestaVocabulario();
                    respuestaVocabulario.setId_RespuestaTest(rs.getInt("id_respuesta_test"));
                    respuestaVocabulario.setIdPreguntaTest(rs.getInt("id_pregunta_test"));
                    respuestaVocabulario.setOpcion(rs.getString("opcion"));
                    respuestaVocabulario.setRespuesta(rs.getString("respuesta"));
                    respuestaVocabulario.setValida(rs.getInt("valida"));
                    respuestaVocabularioList.add(respuestaVocabulario);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al consultar respuestas de vocabulario ");
        }

        return respuestaVocabularioList;
    }

    public List<ResponseRespuestaAbstraccion> obtenerRespuestasAbstraccion() {
        List<ResponseRespuestaAbstraccion> respuestaAbstraccionList = new ArrayList<>();

        //JOIN con pregunta_test filtrando por ABSTRACCION
        String sql = "SELECT r.id_respuesta_test, r.id_pregunta_test, r.respuesta " +
                "FROM respuesta_test r " +
                "JOIN pregunta_test p ON r.id_pregunta_test = p.id_pregunta_test " +
                "WHERE p.tipo_test = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            pstmt.setString(1, "ABSTRACCION");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ResponseRespuestaAbstraccion respuestaAbstraccion = new ResponseRespuestaAbstraccion();
                    respuestaAbstraccion.setId_RespuestaTest(rs.getInt("id_respuesta_test"));
                    respuestaAbstraccion.setIdPreguntaTest(rs.getInt("id_pregunta_test"));
                    respuestaAbstraccion.setRespuesta(rs.getString("respuesta"));
                    respuestaAbstraccionList.add(respuestaAbstraccion);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al consultar respuestas de abstraccion");
        }
        return respuestaAbstraccionList;
    }
}
