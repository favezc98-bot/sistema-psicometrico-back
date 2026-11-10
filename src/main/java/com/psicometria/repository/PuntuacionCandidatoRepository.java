package com.psicometria.repository;

import com.psicometria.conexion.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuntuacionCandidatoRepository {

    public int contarAciertosVocabulario(int idEvaluacion) {
        // Se mantiene la comparación por texto (rt.respuesta) y se usa DISTINCT para evitar duplicados
        String sql = "SELECT COUNT(DISTINCT rc.id_pregunta_test) AS aciertos " +
                "FROM respuesta_candidato rc " +
                "JOIN respuesta_test rt ON rt.id_pregunta_test = rc.id_pregunta_test " +
                "AND rc.respuesta_seleccionada = rt.respuesta " +
                "JOIN pregunta_test p ON p.id_pregunta_test = rc.id_pregunta_test " +
                "WHERE rc.id_evaluacion_fk = ? AND p.tipo_test = 'VOCABULARIO' AND rt.valida = 1";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvaluacion);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("aciertos");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int contarTotalVocabulario(int idEvaluacion) {
        String sql = "SELECT COUNT(DISTINCT rc.id_pregunta_test) AS total " +
                "FROM respuesta_candidato rc " +
                "JOIN pregunta_test p ON p.id_pregunta_test = rc.id_pregunta_test " +
                "WHERE rc.id_evaluacion_fk = ? AND p.tipo_test = 'VOCABULARIO'";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvaluacion);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Map<String, String>> obtenerRespuestasAbstraccion(int idEvaluacion) {
        List<Map<String, String>> lista = new ArrayList<>();
        String sql = "SELECT rc.respuesta_seleccionada, " +
                "CASE WHEN EXISTS (" +
                "SELECT 1 FROM respuesta_test rt " +
                "WHERE rt.id_pregunta_test = rc.id_pregunta_test " +
                "AND LOWER(TRIM(rt.respuesta)) = LOWER(TRIM(rc.respuesta_seleccionada))" +
                ") THEN '1' ELSE '0' END AS correcta " +
                "FROM respuesta_candidato rc " +
                "JOIN pregunta_test p ON p.id_pregunta_test = rc.id_pregunta_test " +
                "WHERE rc.id_evaluacion_fk = ? " +
                "AND p.tipo_test = 'ABSTRACCION'";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvaluacion);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("usuario", rs.getString("respuesta_seleccionada"));
                    map.put("correcta", rs.getString("correcta"));
                    lista.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Map<String, String>> obtenerRespuestasBloques(int idEvaluacion) {
        List<Map<String, String>> lista = new ArrayList<>();
        String sql = "SELECT rc.respuesta_seleccionada, rt.opcion " +
                "FROM respuesta_candidato rc " +
                "JOIN respuesta_test rt ON rt.id_pregunta_test = rc.id_pregunta_test " +
                "JOIN pregunta_test p ON p.id_pregunta_test = rc.id_pregunta_test " +
                "WHERE rc.id_evaluacion_fk = ? AND p.tipo_test = 'BLOQUE' AND rt.valida = 1";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvaluacion);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("usuario", rs.getString("respuesta_seleccionada"));
                    map.put("opcionCorrecta", rs.getString("opcion"));
                    lista.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Map<String, Object> obtenerDatosEvaluacionYEdad(int idEvaluacion) {
        Map<String, Object> datos = new HashMap<>();
        String sql = "SELECT e.combinacion, c.nombre, c.apellido, TIMESTAMPDIFF(YEAR, c.fecha_nacimiento, e.fecha_evaluacion) AS edad " +
                "FROM evaluacion e " +
                "JOIN candidato c ON e.id_candidato_fk = c.id_candidato " +
                "WHERE e.id_evaluacion = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvaluacion);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    datos.put("combinacion", rs.getString("combinacion"));
                    datos.put("nombre", rs.getString("nombre"));       // <-- Agregado
                    datos.put("apellido", rs.getString("apellido"));   // <-- Agregado
                    datos.put("edad", rs.getInt("edad"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datos;
    }

    // Para las tablas individuales: A1, A3, A5, A7, A9, A11, A13, A15
    public String obtenerCodigoTablaIndividualPorEdad(int edad) {
        if (edad <= 19) return "A1";
        if (edad <= 29) return "A3";
        if (edad <= 39) return "A5"; // Para 35 años (Evaluación 49), devuelve A5
        if (edad <= 49) return "A7";
        if (edad <= 59) return "A9";
        if (edad <= 69) return "A11";
        if (edad <= 79) return "A13";
        return "A15";
    }

    // Para las tablas combinadas: A2, A4, A6, A8, A10, A12, A14, A16
    public String obtenerCodigoTablaCombinadaPorEdad(int edad) {
        if (edad <= 19) return "A2";
        if (edad <= 29) return "A4";
        if (edad <= 39) return "A6";
        if (edad <= 49) return "A8";
        if (edad <= 59) return "A10";
        if (edad <= 69) return "A12";
        if (edad <= 79) return "A14";
        return "A16";
    }

    public Map<String, Object> obtenerConversionIndividual(String codigoTabla, String tipoTest, int aciertosNaturales) {
        Map<String, Object> resultado = new HashMap<>();
        String columnaTest = "";

        if ("VOCABULARIO".equalsIgnoreCase(tipoTest)) columnaTest = "vocabulario";
        else if ("ABSTRACCION".equalsIgnoreCase(tipoTest)) columnaTest = "abstraccion";
        else if ("BLOQUE".equalsIgnoreCase(tipoTest)) columnaTest = "bloques";
        else return resultado;

        String sql = "SELECT puntuacion_estandar, percentil FROM puntuacion WHERE codigo_tabla = ? AND " + columnaTest + " = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigoTabla);
            stmt.setInt(2, aciertosNaturales);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Leemos como String primero para evitar excepciones con valores como ">145" o "<70"
                    String estandarStr = rs.getString("puntuacion_estandar");
                    if (estandarStr != null) {
                        // Limpiamos cualquier símbolo y dejamos solo los números
                        String limpio = estandarStr.replaceAll("[^0-9]", "");
                        if (!limpio.isEmpty()) {
                            resultado.put("puntuacionEstandar", Integer.parseInt(limpio));
                        } else {
                            resultado.put("puntuacionEstandar", 0);
                        }
                    } else {
                        resultado.put("puntuacionEstandar", null);
                    }

                    resultado.put("percentil", rs.getString("percentil"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public Map<String, Object> obtenerConversionCombinada(String codigoTabla, String tipoCombinacion, int sumaEstandar) {

        Map<String, Object> resultado = new HashMap<>();
        String columnaSuma;

        if ("AB".equalsIgnoreCase(tipoCombinacion)
                || "A+B".equalsIgnoreCase(tipoCombinacion)) {
            columnaSuma = "combinacion_a_suma_puntuacion";
        } else if ("AC".equalsIgnoreCase(tipoCombinacion)
                || "A+C".equalsIgnoreCase(tipoCombinacion)) {
            columnaSuma = "combinacion_b_suma_puntuacion";
        } else {
            return resultado;
        }

        String sql = "SELECT puntuacion_estandar, percentil_combinacion, "
                + columnaSuma + " AS rango "
                + "FROM combinacion WHERE codigo_combinacion = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoTabla);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String rango = rs.getString("rango");

                    if (rango == null || rango.trim().isEmpty()) {
                        continue;
                    }

                    rango = rango.trim();
                    boolean coincide = false;

                    if (rango.startsWith(">")) {
                        int limite = Integer.parseInt(rango.substring(1).trim());
                        coincide = sumaEstandar > limite;

                    } else if (rango.contains("-") || rango.contains("–")) {
                        String[] limites = rango.split("\\s*[-–]\\s*");

                        int minimo = Integer.parseInt(limites[0].trim());
                        int maximo = Integer.parseInt(limites[1].trim());

                        coincide = sumaEstandar >= minimo
                                && sumaEstandar <= maximo;

                    } else {
                        try {
                            coincide = sumaEstandar == Integer.parseInt(rango);
                        } catch (NumberFormatException e) {
                            // Ignorar valores no numéricos extraños
                        }
                    }

                    if (coincide) {
                        resultado.put("puntuacionEstandarCombinada",
                                rs.getString("puntuacion_estandar"));
                        resultado.put("percentilCombinado",
                                rs.getString("percentil_combinacion"));
                        return resultado;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultado;
    }

    // Consultar la Puntuación T en la Tabla C1 (conversion_total) utilizando la columna real 'puntuacion_total'
    public double obtenerPuntuacionT(int puntuacionEstandar) {
        String sql = "SELECT puntuacion_estandar, puntuacion_total FROM conversion_total";

        double puntuacionTMejor = 0.0;
        int mejorEstEncontrado = -1;

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String rangoEstandar = rs.getString("puntuacion_estandar");
                String valorTotalStr = rs.getString("puntuacion_total");

                if (rangoEstandar == null || rangoEstandar.trim().isEmpty() ||
                        valorTotalStr == null || valorTotalStr.trim().isEmpty()) {
                    continue;
                }

                rangoEstandar = rangoEstandar.trim();
                valorTotalStr = valorTotalStr.trim();

                try {
                    int minimoEst = 0;
                    boolean coincideExactoOrRange = false;

                    if (rangoEstandar.startsWith(">")) {
                        int limite = Integer.parseInt(rangoEstandar.substring(1).trim());
                        minimoEst = limite + 1;
                        if (puntuacionEstandar > limite) {
                            coincideExactoOrRange = true;
                        }
                    } else if (rangoEstandar.contains("-") || rangoEstandar.contains("–")) {
                        String[] limites = rangoEstandar.split("\\s*[-–]\\s*");
                        minimoEst = Integer.parseInt(limites[0].trim());
                        int maximo = Integer.parseInt(limites[1].trim());
                        if (puntuacionEstandar >= minimoEst && puntuacionEstandar <= maximo) {
                            coincideExactoOrRange = true;
                        }
                    } else {
                        minimoEst = Integer.parseInt(rangoEstandar);
                        if (puntuacionEstandar == minimoEst) {
                            coincideExactoOrRange = true;
                        }
                    }

                    // Parsear el valor T correspondiente (por si también es rango como "64-65")
                    double valorTActual = 0.0;
                    if (valorTotalStr.contains("-") || valorTotalStr.contains("–")) {
                        String[] subLimites = valorTotalStr.split("\\s*[-–]\\s*");
                        valorTActual = Double.parseDouble(subLimites[0].trim());
                    } else {
                        valorTActual = Double.parseDouble(valorTotalStr);
                    }

                    // Si hay coincidencia exacta o de rango, lo retornamos de inmediato
                    if (coincideExactoOrRange) {
                        return valorTActual;
                    }

                    // Si no hay coincidencia exacta, buscamos el valor estándar más cercano por debajo (Floor Lookup)
                    if (puntuacionEstandar >= minimoEst && minimoEst > mejorEstEncontrado) {
                        mejorEstEncontrado = minimoEst;
                        puntuacionTMejor = valorTActual;
                    }

                } catch (NumberFormatException ex) {
                    // Ignorar filas con formatos no numéricos
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Si no hubo coincidencia exacta, devolvemos el aproximado inferior encontrado (ej. el de 122 para 123)
        return puntuacionTMejor;
    }

    public List<Map<String, Object>> obtenerHistorialPorCandidato(int idCandidato) {
        List<Map<String, Object>> historial = new ArrayList<>();
        String sql = "SELECT id_evaluacion, combinacion, estado, fecha_evaluacion " +
                "FROM evaluacion " +
                "WHERE id_candidato_fk = ? " +
                "ORDER BY fecha_evaluacion DESC " +
                "LIMIT 2";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCandidato);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> eval = new HashMap<>();
                    eval.put("idEvaluacion", rs.getInt("id_evaluacion"));
                    eval.put("combinacion", rs.getString("combinacion"));
                    eval.put("estado", rs.getString("estado"));
                    eval.put("fechaEvaluacion", rs.getString("fecha_evaluacion"));
                    historial.add(eval);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return historial;
    }
}