package com.psicometria.service;

import com.psicometria.conexion.ConexionBD;
import com.psicometria.repository.PuntuacionCandidatoRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        if (combinacion == null) {
            return false;
        }

        combinacion = combinacion.trim().toUpperCase();

        // Validar que estrictamente sea una de las combinaciones soportadas por el sistema
        if (!combinacion.equals("AB") && !combinacion.equals("A+B") &&
                !combinacion.equals("AC") && !combinacion.equals("A+C")) {
            System.err.println("Combinación de pruebas no válida: " + combinacion);
            return false;
        }

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

    //Obtener la combinación solo si la evaluación sigue en estado 'PROGRAMADA'
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

    //Guardar paquete masivo de respuestas y cambiar estado a FINALIZADA
    public boolean guardarRespuestasYFinalizar(int idEvaluacion, List<modelo.RespuestaCandidato> respuestas) {
        String sqlInsert = "INSERT INTO respuesta_candidato (id_evaluacion_fk, id_pregunta_test, sub_parte, respuesta_seleccionada) VALUES (?, ?, ?, ?)";
        String sqlUpdateEstado = "UPDATE evaluacion SET estado = 'FINALIZADA' WHERE id_evaluacion = ?";

        Connection conn = null;

        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false); // Transacción manual

            // A. Inserción masiva usando el modelo RespuestaCandidato
            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                for (modelo.RespuestaCandidato item : respuestas) {
                    stmtInsert.setInt(1, idEvaluacion); // <--- Guarda el ID de la evaluación en la FK
                    stmtInsert.setInt(2, item.getIdPreguntaTest());
                    stmtInsert.setString(3, item.getSubParte());
                    stmtInsert.setString(4, item.getRespuestaSeleccionada());
                    stmtInsert.addBatch();
                }
                stmtInsert.executeBatch();
            }

            // B. Cambiar el estado de la evaluación a FINALIZADA usando su ID exacto
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdateEstado)) {
                stmtUpdate.setInt(1, idEvaluacion);
                stmtUpdate.executeUpdate();
            }

            conn.commit();
            conn.close();
            return true;

        } catch (Exception e) {
            System.err.println("Error al guardar respuestas y finalizar evaluación: " + e.getMessage());
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

    // Obtener tanto el id_evaluacion como la combinación activa
    public Map<String, Object> obtenerDatosUltimaEvaluacion(int idCandidato) {
        Map<String, Object> resultado = new HashMap<>();
        String sql = "SELECT id_evaluacion, combinacion FROM evaluacion WHERE id_candidato_fk = ? AND estado = 'PROGRAMADA' ORDER BY id_evaluacion DESC LIMIT 1";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCandidato);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                resultado.put("id_evaluacion", rs.getInt("id_evaluacion"));
                resultado.put("combinacion", rs.getString("combinacion"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public Map<String, Object> consultarPuntuacion(int idEvaluacion) {
        Map<String, Object> respuestaGeneral = new HashMap<>();
        Map<String, Object> resultadoMap = new HashMap<>();

        String sql = "SELECT e.id_evaluacion, e.combinacion, c.nombre, c.apellido, " +
                "TIMESTAMPDIFF(YEAR, c.fecha_nacimiento, CURDATE()) AS edad " +
                "FROM evaluacion e " +
                "JOIN candidato c ON e.id_candidato_fk = c.id_candidato " +
                "WHERE e.id_evaluacion = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEvaluacion);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                respuestaGeneral.put("encontrado", true);

                resultadoMap.put("idEvaluacion", rs.getInt("id_evaluacion"));
                resultadoMap.put("nombre", rs.getString("nombre"));
                resultadoMap.put("apellido", rs.getString("apellido"));
                resultadoMap.put("edadEvaluado", rs.getInt("edad"));
                String combinacion = rs.getString("combinacion");
                resultadoMap.put("combinacion", combinacion);

                //Obtener las pruebas individuales y sus conversiones
                List<Map<String, Object>> listaPruebas = obtenerPruebasYConversiones(idEvaluacion, rs.getInt("edad"), combinacion);
                resultadoMap.put("pruebas", listaPruebas);
                //Calcular el resultado combinado (Asegurando null en lugar de "0" si no hay baremo)
                Map<String, Object> resultadoCombinado = calcularResultadoCombinado(listaPruebas, rs.getInt("edad"), combinacion);
                // Forzar null si la puntuación estándar combinada es "0" o vacía
                Object puntEstComb = resultadoCombinado.get("puntuacionEstandar");
                if (puntEstComb == null || String.valueOf(puntEstComb).equals("0") || String.valueOf(puntEstComb).equals("null")) {
                    resultadoCombinado.put("puntuacionEstandar", null);
                    resultadoCombinado.put("percentil", null);
                    resultadoCombinado.put("categoria", "Sin baremo combinado exacto");
                }

                resultadoMap.put("resultadoCombinado", resultadoCombinado);
                resultadoMap.put("categoria", resultadoCombinado.get("categoria"));
                resultadoMap.put("puntajeTotal", calcularPuntajeTotal(listaPruebas));

                respuestaGeneral.put("resultado", resultadoMap);
            } else {
                respuestaGeneral.put("encontrado", false);
                respuestaGeneral.put("mensaje", "Evaluación no encontrada");
            }

        } catch (Exception e) {
            e.printStackTrace();
            respuestaGeneral.put("encontrado", false);
            respuestaGeneral.put("error", e.getMessage());
        }
        return respuestaGeneral;
    }

    // Método auxiliar para obtener las pruebas y sus conversiones individuales
    public List<Map<String, Object>> obtenerPruebasYConversiones(int idEvaluacion, int edad, String combinacion) {
        List<Map<String, Object>> listaPruebas = new ArrayList<>();
        PuntuacionCandidatoRepository repo = new PuntuacionCandidatoRepository();
        String codigoTablaInd = repo.obtenerCodigoTablaIndividualPorEdad(edad);

        combinacion = combinacion.toUpperCase();
        boolean esAB = combinacion.contains("AB");

        //Vocabulario (Presente en ambas combinaciones AB y AC)
        int aciertosVocab = repo.contarAciertosVocabulario(idEvaluacion);
        Map<String, Object> convVocab = repo.obtenerConversionIndividual(codigoTablaInd, "VOCABULARIO", aciertosVocab);

        Map<String, Object> mapVocab = new HashMap<>();
        mapVocab.put("tipo", "VOCABULARIO");
        mapVocab.put("nombre", "Vocabulario");
        mapVocab.put("puntuacionNatural", aciertosVocab);
        mapVocab.put("puntuacionEstandar", convVocab.get("puntuacionEstandar"));
        mapVocab.put("percentil", convVocab.get("percentil"));
        // Calcular Puntuación T si hay puntuación estándar válida
        Object estVocab = convVocab.get("puntuacionEstandar");
        double tVocab = 0.0;
        if (estVocab != null) {
            tVocab = repo.obtenerPuntuacionT(Integer.parseInt(estVocab.toString()));
        }
        mapVocab.put("puntuacionT", tVocab);
        listaPruebas.add(mapVocab);

        // 2. Segunda Prueba (Abstracción si es AB, o Bloques si es AC)
        if (esAB) {
            List<Map<String, String>> respAbst = repo.obtenerRespuestasAbstraccion(idEvaluacion);
            int aciertosAbst = 0;
            for (Map<String, String> r : respAbst) {
                if ("1".equals(r.get("correcta"))) aciertosAbst++;
            }
            Map<String, Object> convAbst = repo.obtenerConversionIndividual(codigoTablaInd, "ABSTRACCION", aciertosAbst);

            Map<String, Object> mapAbst = new HashMap<>();
            mapAbst.put("tipo", "ABSTRACCION");
            mapAbst.put("nombre", "Abstracción");
            mapAbst.put("puntuacionNatural", aciertosAbst);
            mapAbst.put("puntuacionEstandar", convAbst.get("puntuacionEstandar"));
            mapAbst.put("percentil", convAbst.get("percentil"));
            Object estAbst = convAbst.get("puntuacionEstandar");
            double tAbst = 0.0;
            if (estAbst != null) {
                tAbst = repo.obtenerPuntuacionT(Integer.parseInt(estAbst.toString()));
            }
            mapAbst.put("puntuacionT", tAbst);
            listaPruebas.add(mapAbst);

        } else {
            List<Map<String, String>> respBloques = repo.obtenerRespuestasBloques(idEvaluacion);
            int aciertosBloques = 0;
            for (Map<String, String> r : respBloques) {
                if (r.get("usuario") != null && r.get("usuario").trim().equalsIgnoreCase(r.get("opcionCorrecta"))) {
                    aciertosBloques++;
                }
            }
            Map<String, Object> convBloques = repo.obtenerConversionIndividual(codigoTablaInd, "BLOQUE", aciertosBloques);

            Map<String, Object> mapBloques = new HashMap<>();
            mapBloques.put("tipo", "BLOQUE");
            mapBloques.put("nombre", "Bloques");
            mapBloques.put("puntuacionNatural", aciertosBloques);
            mapBloques.put("puntuacionEstandar", convBloques.get("puntuacionEstandar"));
            mapBloques.put("percentil", convBloques.get("percentil"));
            Object estBloques = convBloques.get("puntuacionEstandar");
            double tBloques = 0.0;
            if (estBloques != null) {
                tBloques = repo.obtenerPuntuacionT(Integer.parseInt(estBloques.toString()));
            }
            mapBloques.put("puntuacionT", tBloques);
            listaPruebas.add(mapBloques);
        }

        return listaPruebas;
    }

    // Método auxiliar para calcular el resultado combinado
    public Map<String, Object> calcularResultadoCombinado(List<Map<String, Object>> listaPruebas, int edad, String combinacion) {
            Map<String, Object> resultadoComb = new HashMap<>();
            PuntuacionCandidatoRepository repo = new PuntuacionCandidatoRepository();
            String codigoTablaComb = repo.obtenerCodigoTablaCombinadaPorEdad(edad);

            int sumaEstandar = 0;
            boolean incompleto = false;

            for (Map<String, Object> prueba : listaPruebas) {
                Object est = prueba.get("puntuacionEstandar");
                if (est == null) {
                    incompleto = true;
                } else {
                    sumaEstandar += Integer.parseInt(est.toString());
                }
            }

            resultadoComb.put("sumaPuntuacionesEstandar", sumaEstandar);

            if (incompleto || sumaEstandar == 0) {
                resultadoComb.put("puntuacionEstandar", null);
                resultadoComb.put("percentil", null);
                resultadoComb.put("categoria", "Conversión incompleta (Faltan baremos individuales)");
                return resultadoComb;
            }

            Map<String, Object> convComb = repo.obtenerConversionCombinada(codigoTablaComb, combinacion, sumaEstandar);

            if (convComb.isEmpty() || convComb.get("puntuacionEstandarCombinada") == null) {
                resultadoComb.put("puntuacionEstandar", null);
                resultadoComb.put("percentil", null);
                resultadoComb.put("categoria", "Sin baremo combinado exacto");
            } else {
                resultadoComb.put("puntuacionEstandar", convComb.get("puntuacionEstandarCombinada"));
                resultadoComb.put("percentil", convComb.get("percentilCombinado"));
                resultadoComb.put("categoria", "Resultado Procesado Correctamente");
            }

            return resultadoComb;
        }

        // Método auxiliar para calcular el puntaje total (suma de aciertos naturales)
        public int calcularPuntajeTotal (List < Map < String, Object >> listaPruebas){
            int total = 0;
            for (Map<String, Object> prueba : listaPruebas) {
                Object nat = prueba.get("puntuacionNatural");
                if (nat != null) {
                    total += Integer.parseInt(nat.toString());
                }
            }
            return total;
        }

}