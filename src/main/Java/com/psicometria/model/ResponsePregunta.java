package com.psicometria.model;

public class ResponsePregunta {

    private int idPreguntaTest;
    private String tipoTest;
    private int numeroReactivo;
    private String pregunta;

    // Getters y Setters
    public int getIdPreguntaTest() {
        return idPreguntaTest;
    }
    public void setIdPreguntaTest(int idPreguntaTest) {
        this.idPreguntaTest = idPreguntaTest;
    }

    public String getTipoTest() {
        return tipoTest; }
    public void setTipoTest(String tipoTest) {
        this.tipoTest = tipoTest;
    }

    public int getNumeroReactivo() { return numeroReactivo; }
    public void setNumeroReactivo(int numeroReactivo) { this.numeroReactivo = numeroReactivo; }

    public String getPregunta() { return pregunta; }
    public void setPregunta(String pregunta) { this.pregunta = pregunta; }
}
