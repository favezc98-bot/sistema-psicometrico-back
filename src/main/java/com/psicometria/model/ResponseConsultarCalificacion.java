package com.psicometria.model;

public class ResponseConsultarCalificacion {

    private int idEvaluacion;

    private int aciertosVocabulario;

    private int aciertosAbstraccion;

    private int aciertosBloques;

    private int totalReactivosVocabulario;

    private int totalReactivosAbstraccion;

    private int totalSubPartesBloques;

    private int puntajeTotal;

    public ResponseConsultarCalificacion() {
    }

    public ResponseConsultarCalificacion(int idEvaluacion, int aciertosVocabulario,
                                         int aciertosAbstraccion, int aciertosBloques,
                                         int totalReactivosVocabulario, int totalReactivosAbstraccion,
                                         int totalSubPartesBloques, int puntajeTotal) {
        this.idEvaluacion = idEvaluacion;
        this.aciertosVocabulario = aciertosVocabulario;
        this.aciertosAbstraccion = aciertosAbstraccion;
        this.aciertosBloques = aciertosBloques;
        this.totalReactivosVocabulario = totalReactivosVocabulario;
        this.totalReactivosAbstraccion = totalReactivosAbstraccion;
        this.totalSubPartesBloques = totalSubPartesBloques;
        this.puntajeTotal = puntajeTotal;
    }

    public int getIdEvaluacion() {
        return idEvaluacion;
    }

    public void setIdEvaluacion(int idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }

    public int getAciertosVocabulario() {
        return aciertosVocabulario;
    }

    public void setAciertosVocabulario(int aciertosVocabulario) {
        this.aciertosVocabulario = aciertosVocabulario;
    }

    public int getAciertosAbstraccion() {
        return aciertosAbstraccion;
    }

    public void setAciertosAbstraccion(int aciertosAbstraccion) {
        this.aciertosAbstraccion = aciertosAbstraccion;
    }

    public int getAciertosBloques() {
        return aciertosBloques;
    }

    public void setAciertosBloques(int aciertosBloques) {
        this.aciertosBloques = aciertosBloques;
    }

    public int getTotalReactivosVocabulario() {
        return totalReactivosVocabulario;
    }

    public void setTotalReactivosVocabulario(int totalReactivosVocabulario) {
        this.totalReactivosVocabulario = totalReactivosVocabulario;
    }

    public int getTotalReactivosAbstraccion() {
        return totalReactivosAbstraccion;
    }

    public void setTotalReactivosAbstraccion(int totalReactivosAbstraccion) {
        this.totalReactivosAbstraccion = totalReactivosAbstraccion;
    }

    public int getTotalSubPartesBloques() {
        return totalSubPartesBloques;
    }

    public void setTotalSubPartesBloques(int totalSubPartesBloques) {
        this.totalSubPartesBloques = totalSubPartesBloques;
    }

    public int getPuntajeTotal() {
        return puntajeTotal;
    }

    public void setPuntajeTotal(int puntajeTotal) {
        this.puntajeTotal = puntajeTotal;
    }
}
