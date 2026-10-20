package com.psicometria.model;

public class ResponseRespuestaVocabulario {

    private int id_RespuestaTest;

    private int idPreguntaTest;

    private String opcion;

    private String respuesta;

    private int valida;

    public ResponseRespuestaVocabulario(int id_RespuestaTest, int idPreguntaTest, String opcion, String respuesta, int valida) {
        this.id_RespuestaTest = id_RespuestaTest;
        this.idPreguntaTest = idPreguntaTest;
        this.opcion = opcion;
        this.respuesta = respuesta;
        this.valida = valida;
    }

    public ResponseRespuestaVocabulario() {

    }

    public int getId_RespuestaTest() {
        return id_RespuestaTest;
    }

    public void setId_RespuestaTest(int id_RespuestaTest) {
        this.id_RespuestaTest = id_RespuestaTest;
    }

    public int getIdPreguntaTest() {
        return idPreguntaTest;
    }

    public void setIdPreguntaTest(int idPreguntaTest) {
        this.idPreguntaTest = idPreguntaTest;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public int getValida() {
        return valida;
    }

    public void setValida(int valida) {
        this.valida = valida;
    }
}
