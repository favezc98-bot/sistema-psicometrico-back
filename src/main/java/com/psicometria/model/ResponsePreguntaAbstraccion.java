package com.psicometria.model;

public class ResponsePreguntaAbstraccion {

    private int idPreguntaTest;
    private String tipoTest;
    private int numeroReactivo;
    private String pregunta;

    public ResponsePreguntaAbstraccion(int idPreguntaTest, String tipoTest, int numeroReactivo, String pregunta) {
        this.idPreguntaTest = idPreguntaTest;
        this.tipoTest = tipoTest;
        this.numeroReactivo = numeroReactivo;
        this.pregunta = pregunta;
    }

    public ResponsePreguntaAbstraccion() {
    }

    public int getIdPreguntaTest() {
        return idPreguntaTest;
    }

    public void setIdPreguntaTest(int idPreguntaTest) {
        this.idPreguntaTest = idPreguntaTest;
    }

    public String getTipoTest() {
        return tipoTest;
    }

    public void setTipoTest(String tipoTest) {
        this.tipoTest = tipoTest;
    }

    public int getNumeroReactivo() {
        return numeroReactivo;
    }

    public void setNumeroReactivo(int numeroReactivo) {
        this.numeroReactivo = numeroReactivo;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }
}
