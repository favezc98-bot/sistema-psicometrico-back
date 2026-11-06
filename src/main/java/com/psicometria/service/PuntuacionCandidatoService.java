package com.psicometria.service;

import com.psicometria.model.ResponsePruebaDetalle;
import com.psicometria.model.ResponseResultadoCombinado;
import com.psicometria.model.ResponseResultadoEvaluacion;
import com.psicometria.repository.PuntuacionCandidatoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PuntuacionCandidatoService {

    private final PuntuacionCandidatoRepository repository = new PuntuacionCandidatoRepository();

    public ResponseResultadoEvaluacion calcularResultadoCompleto(int idEvaluacion) {
        ResponseResultadoEvaluacion resultadoFinal = new ResponseResultadoEvaluacion();
        resultadoFinal.setIdEvaluacion(idEvaluacion);

        // Obtener datos generales de la evaluación
        Map<String, Object> datosEval = repository.obtenerDatosEvaluacionYEdad(idEvaluacion);
        String combinacion = (String) datosEval.get("combinacion");
        int edad = datosEval.get("edad") != null ? (int) datosEval.get("edad") : 0;

        combinacion = combinacion != null ? combinacion.trim().toUpperCase() : "AB";
        resultadoFinal.setCombinacion(combinacion);
        resultadoFinal.setEdadEvaluado(edad);

        // Separar códigos normativos: Individuales (ej. A5) y Combinados (ej. A6)
        String codigoTablaIndividual = repository.obtenerCodigoTablaIndividualPorEdad(edad);
        String codigoTablaCombinada = repository.obtenerCodigoTablaCombinadaPorEdad(edad);

        List<ResponsePruebaDetalle> listaPruebas = new ArrayList<>();

        int aciertosVocab = 0;
        int estandarVocab = 0;
        int aciertosAbst = 0;
        int aciertosBloques = 0;
        int estandarSegundaPrueba = 0;

        // Mapas para validar si las conversiones individuales existen realmente
        Map<String, Object> convVocab = null;
        Map<String, Object> convSegundaPrueba = null;

        // CALCULAR VOCABULARIO
        aciertosVocab = repository.contarAciertosVocabulario(idEvaluacion);
        convVocab = repository.obtenerConversionIndividual(codigoTablaIndividual, "VOCABULARIO", aciertosVocab);

        if (convVocab != null && !convVocab.isEmpty() && convVocab.get("puntuacionEstandar") != null) {
            estandarVocab = (int) convVocab.get("puntuacionEstandar");
        }

        String percentilVocab = convVocab != null ? (String) convVocab.get("percentil") : null;
        double tVocab = repository.obtenerPuntuacionT(estandarVocab);

        listaPruebas.add(new ResponsePruebaDetalle("VOCABULARIO", "Vocabulario", aciertosVocab, estandarVocab, percentilVocab, tVocab));

        // CALCULAR ABSTRACCIÓN O BLOQUES
        if ("AB".equalsIgnoreCase(combinacion) || "A+B".equalsIgnoreCase(combinacion)) {
            List<Map<String, String>> abstracciones = repository.obtenerRespuestasAbstraccion(idEvaluacion);
            for (Map<String, String> item : abstracciones) {
                if ("1".equals(item.get("correcta"))) {
                    aciertosAbst++;
                }
            }
            convSegundaPrueba = repository.obtenerConversionIndividual(codigoTablaIndividual, "ABSTRACCION", aciertosAbst);

            if (convSegundaPrueba != null && !convSegundaPrueba.isEmpty() && convSegundaPrueba.get("puntuacionEstandar") != null) {
                estandarSegundaPrueba = (int) convSegundaPrueba.get("puntuacionEstandar");
            }

            String percentilAbst = convSegundaPrueba != null ? (String) convSegundaPrueba.get("percentil") : null;
            double tAbst = repository.obtenerPuntuacionT(estandarSegundaPrueba);

            listaPruebas.add(new ResponsePruebaDetalle("ABSTRACCION", "Abstracción", aciertosAbst, estandarSegundaPrueba, percentilAbst, tAbst));
        } else {
            List<Map<String, String>> bloques = repository.obtenerRespuestasBloques(idEvaluacion);
            for (Map<String, String> item : bloques) {
                String u = item.get("usuario");
                String c = item.get("opcionCorrecta");
                if (u != null && c != null && u.trim().equalsIgnoreCase(c.trim())) {
                    aciertosBloques++;
                }
            }
            convSegundaPrueba = repository.obtenerConversionIndividual(codigoTablaIndividual, "BLOQUE", aciertosBloques);

            if (convSegundaPrueba != null && !convSegundaPrueba.isEmpty() && convSegundaPrueba.get("puntuacionEstandar") != null) {
                estandarSegundaPrueba = (int) convSegundaPrueba.get("puntuacionEstandar");
            }

            String percentilBloques = convSegundaPrueba != null ? (String) convSegundaPrueba.get("percentil") : null;
            double tBloques = repository.obtenerPuntuacionT(estandarSegundaPrueba);

            listaPruebas.add(new ResponsePruebaDetalle("BLOQUE", "Bloques", aciertosBloques, estandarSegundaPrueba, percentilBloques, tBloques));
        }

        resultadoFinal.setPruebas(listaPruebas);
        resultadoFinal.setPuntajeTotal(aciertosVocab + aciertosAbst + aciertosBloques);

        // Validación: Si alguna conversión individual es vacía o es 0,
        // detenemos la combinación para no generar datos falsos.
        if (convVocab == null || convVocab.isEmpty() || convSegundaPrueba == null || convSegundaPrueba.isEmpty() || estandarVocab == 0 || estandarSegundaPrueba == 0) {
            ResponseResultadoCombinado combDTO = new ResponseResultadoCombinado();
            combDTO.setSumaPuntuacionesEstandar(0);
            combDTO.setPuntuacionEstandar(null);
            combDTO.setPercentil(null);
            combDTO.setCategoria("Conversión incompleta (Faltan baremos individuales)");

            resultadoFinal.setResultadoCombinado(combDTO);
            resultadoFinal.setCategoria("Conversión incompleta");
            return resultadoFinal;
        }

        // CONVERSIÓN COMBINADA (Usando el código combinado correcto, ej. A6)
        int sumaEstandar = estandarVocab + estandarSegundaPrueba;
        String tipoCombBusqueda = ("AB".equalsIgnoreCase(combinacion) || "A+B".equalsIgnoreCase(combinacion)) ? "AB" : "BC";

        ResponseResultadoCombinado combDTO = new ResponseResultadoCombinado();
        combDTO.setSumaPuntuacionesEstandar(sumaEstandar);

        Map<String, Object> convCombinada = repository.obtenerConversionCombinada(codigoTablaCombinada, tipoCombBusqueda, sumaEstandar);
        if (convCombinada != null && !convCombinada.isEmpty()) {
            Object objEstandarComb = convCombinada.get("puntuacionEstandarCombinada");
            combDTO.setPuntuacionEstandar(objEstandarComb != null ? objEstandarComb.toString() : "0");
            combDTO.setPercentil((String) convCombinada.get("percentilCombinado"));
            combDTO.setCategoria("Resultado Procesado Correctamente");
            resultadoFinal.setCategoria("Resultado Procesado Correctamente");
        } else {
            combDTO.setPuntuacionEstandar("0");
            combDTO.setPercentil(null);
            combDTO.setCategoria("Sin baremo combinado exacto");
            resultadoFinal.setCategoria("Sin baremo combinado exacto");
        }

        resultadoFinal.setResultadoCombinado(combDTO);

        return resultadoFinal;
    }
}