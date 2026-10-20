package com.psicometria.model;

public class ResponseRespuestaAbstraccion {

    private int id_RespuestaTest;

    private int idPreguntaTest;

    //private String opcion;

    private String respuesta;

    //private int valida;

    public ResponseRespuestaAbstraccion(int id_RespuestaTest, int idPreguntaTest, String respuesta) {
        this.id_RespuestaTest = id_RespuestaTest;
        this.idPreguntaTest = idPreguntaTest;
        this.respuesta = respuesta;
    }

    public ResponseRespuestaAbstraccion() {
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

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
}
