package com.psicometria.service;

import com.psicometria.model.ResponseConsultarCalificacion;
import com.psicometria.repository.PuntuacionCandidatoRepository;

import java.util.List;
import java.util.Map;

public class PuntuacionCandidatoService {

    private final PuntuacionCandidatoRepository repository = new PuntuacionCandidatoRepository();

    public ResponseConsultarCalificacion obtenerPuntuacionCandidato(int idEvaluacion) {
        ResponseConsultarCalificacion resultado = new ResponseConsultarCalificacion();
        resultado.setIdEvaluacion(idEvaluacion);

        //Vocabulario (Calculado directo en BD)
        int aciertosVocabulario = repository.contarAciertosVocabulario(idEvaluacion);
        int totalVocabulario = repository.contarTotalVocabulario(idEvaluacion);

        // Abstracción (Lógica con equalsIgnoreCase)
        List<Map<String, String>> abstracciones = repository.obtenerRespuestasAbstraccion(idEvaluacion);
        int aciertosAbstraccion = 0;
        for (Map<String, String> item : abstracciones) {
            String u = item.get("usuario");
            String c = item.get("correcta");
            if (u != null && c != null && u.trim().equalsIgnoreCase(c.trim())) {
                aciertosAbstraccion++;
            }
        }

        //Bloques (Evaluación por subpartes)
        List<Map<String, String>> bloques = repository.obtenerRespuestasBloques(idEvaluacion);
        int aciertosBloques = 0;
        for (Map<String, String> item : bloques) {
            String u = item.get("usuario");
            String c = item.get("opcionCorrecta");
            if (u != null && c != null && u.trim().equalsIgnoreCase(c.trim())) {
                aciertosBloques++;
            }
        }

        // Rellenar DTO
        resultado.setAciertosVocabulario(aciertosVocabulario);
        resultado.setTotalReactivosVocabulario(totalVocabulario);

        resultado.setAciertosAbstraccion(aciertosAbstraccion);
        resultado.setTotalReactivosAbstraccion(abstracciones.size());

        resultado.setAciertosBloques(aciertosBloques);
        resultado.setTotalSubPartesBloques(bloques.size());

        resultado.setPuntajeTotal(aciertosVocabulario + aciertosAbstraccion + aciertosBloques);

        return resultado;
    }
}